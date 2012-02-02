package de.jaetzold.dalliklick

import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.GeneralPath
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

/**
 *
 * @author Stephan Jaetzold
 * <p><small>Created at 28.01.12, 13:57</small>
 */
class DalliKlickSequence {
    private final Color SEGMENT_COLOR = Color.WHITE
    private final Color COUNTER_COLOR = Color.BLACK
    private final Font COUNTER_FONT
    private final BufferedImage sourceImage
    private final int width
    private final int height

    public DalliKlickSequence(BufferedImage sourceImage) {
        this.sourceImage = sourceImage
        width = sourceImage.getWidth(null)
        height = sourceImage.getHeight(null)
        COUNTER_FONT = new Font("SansSerif", Font.BOLD, (Math.min(width,height)/6).intValue())
    }

    // note: always returns one image more than elements in revealSequence which is the full image
    // note: it can be tried but is unsupported to have a revealSequence that does not have 1, revealSequence.size() and all numbers in between in it
    public List<BufferedImage> generateImages(List<Integer> revealSequence, boolean centerCircle, boolean addCounter) {
        Integer counterSegment = null;
        if(addCounter) {
            if(centerCircle) {
                counterSegment = 0;
            } else {
                counterSegment = revealSequence.last();
            }
        }
        generateImagesRecursive(revealSequence.max(), revealSequence, centerCircle, counterSegment).reverse()
    }
    
    private List<BufferedImage> generateImagesRecursive(int numberOfSegments, List<Integer> revealSequence, boolean centerCircle, Integer counterSegment) {
        List<BufferedImage> result;
        if(revealSequence.isEmpty()) {
            result = [generateImage(numberOfSegments, [], centerCircle, centerCircle, counterSegment)]
            if(centerCircle) {
                result.add(0, generateImage(numberOfSegments, [], centerCircle, false, counterSegment))
            }
        } else {
            result = generateImagesRecursive(numberOfSegments, revealSequence.tail(), centerCircle, counterSegment)
            result.add(generateImage(numberOfSegments, revealSequence, centerCircle, centerCircle, counterSegment))
        }
        return result;
    }
    
    public BufferedImage generateImage(int numberOfSegments, List<Integer> hiddenSegments, boolean centerCircle, boolean hideInnerCircle, Integer counterSegment) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = result.createGraphics();
        g.drawImage(sourceImage, 0, 0, null);
        g.setColor(SEGMENT_COLOR);
        double diameter = Math.min(width, height)/2.0;
        Ellipse2D circle = new Ellipse2D.Double(width / 2.0 - diameter / 2.0, height / 2.0 - diameter / 2.0, diameter, diameter)
        for(segment in hiddenSegments) {
            // draw segment black onto result
            GeneralPath path = new GeneralPath();
            path.moveTo(width/2.0, height/2.0);

            Point2D.Double p1 = computeOuterSegmentPoint(numberOfSegments, segment-1, width, height)
            Point2D.Double p2 = computeOuterSegmentPoint(numberOfSegments, segment, width, height)

            path.lineTo(p1.x+width/2.0, p1.y+height/2.0);
            if(p1.x!=p2.x && p1.y!=p2.y) {
                if(p2.x>p1.x && p2.y>p1.y) {
                    path.lineTo((double)width, 0);
                } else if(p2.x<p1.x && p2.y>p1.y) {
                    path.lineTo((double)width, height);
                } else if(p2.x<p1.x && p2.y<p1.y) {
                    path.lineTo((double)0, height);
                } else if(p2.x>p1.x && p2.y<p1.y) {
                    path.lineTo((double)0, 0);
                }
            }
            path.lineTo(p2.x+width/2.0, p2.y+height/2.0);
            path.closePath();

            if(centerCircle) {
                Area slice = new Area(path);
                slice.subtract(new Area(circle));
                g.fill(slice);
            } else {
                g.fill(path);
            }
        }
        if(hideInnerCircle) {
            g.fill(new Ellipse2D.Double(circle.x-1, circle.y-1, circle.width+2, circle.height+2));
        }
        if(counterSegment!=null && (!hiddenSegments.isEmpty() || hideInnerCircle)) {
            int count = hiddenSegments.size()+(hideInnerCircle ? 1 : 0);
            String counterString = "" + count
            g.setColor(COUNTER_COLOR);
            g.setFont(COUNTER_FONT);
            FontMetrics metrics = g.getFontMetrics(COUNTER_FONT);
            int counterHeight = metrics.getHeight();
            int counterWidth = metrics.stringWidth(counterString);
            Point2D counterPosition;
            if(counterSegment==0) {
                counterPosition = new Point2D.Double((width-counterWidth)/2.0, (height+counterHeight/2)/2.0);
            } else {
                counterPosition = computeOuterSegmentPoint(numberOfSegments*2, counterSegment*2-1,
                                                           (width*0.9-counterWidth).intValue(), (height*0.9-counterHeight).intValue())
                counterPosition.setLocation(counterPosition.x+(width-counterWidth)/2.0, counterPosition.y+(height+counterHeight/2)/2.0);
            }
            println counterPosition
            g.drawString(counterString, counterPosition.x.floatValue(), counterPosition.y.floatValue())
        }

        return result;
    }

    private static Point2D.Double computeOuterSegmentPoint(int numberOfSegments, int segment, int width, int height) {
        Point2D.Double result = new Point2D.Double();
        int i = segment % numberOfSegments;
        if(i<0) {
            i += numberOfSegments;
        }
        double deg = i * 360.0 / numberOfSegments;
        boolean horizontal;
        double normalizedAngle;
        double oppositeFactor;
        switch(deg) {
            case {it>=-45 && it<45}:
                horizontal = true;
                normalizedAngle = deg;
                oppositeFactor = -1;
                break;
            case {it>=45 && it<135}:
                horizontal = false;
                normalizedAngle = deg-90;
                oppositeFactor = 1;
                break;
            case {it>=135 && it<225}:
                horizontal = true;
                normalizedAngle = (deg-180)*-1;
                oppositeFactor = 1;
                break;
            case {it>=225 && it<315}:
                horizontal = false;
                normalizedAngle = (deg-270)*-1;
                oppositeFactor = -1;
                break;
            case {it>=315}:
                horizontal = true;
                normalizedAngle = deg-360;
                oppositeFactor = -1;
                break;
        }
        double tan = Math.tan(Math.toRadians(normalizedAngle));
        if(horizontal) {
            result.setLocation(tan * width / 2.0, oppositeFactor * height / 2.0);
        } else {
            result.setLocation(oppositeFactor*width/2.0, tan*height/2.0);
        }
        return result;
    }

    public static void main(String[] argv) {
        Point2D.Double point = computeOuterSegmentPoint(12, -1, 200, 200)
        println(point)
        point = computeOuterSegmentPoint(12, 0, 200, 200);
        println(point)
        point = computeOuterSegmentPoint(12, 1, 200, 200);
        println(point)
        point = computeOuterSegmentPoint(12, 2, 200, 200);
        println(point)
        point = computeOuterSegmentPoint(12, 3, 200, 200);
        println(point)
        point = computeOuterSegmentPoint(12, 4, 200, 200);
        println(point)
        point = computeOuterSegmentPoint(12, 5, 200, 200);
        println(point)
        point = computeOuterSegmentPoint(12, 6, 200, 200);
        println(point)
        point = computeOuterSegmentPoint(12, 7, 200, 200);
        println(point)
        point = computeOuterSegmentPoint(12, 8, 200, 200);
        println(point)
    }
}
