package de.jaetzold.dalliklick

import java.awt.image.BufferedImage
import javax.imageio.ImageIO;

/** @author Stephan Jaetzold <p><small>Created at 28.01.12, 13:31</small> */
public class DalliKlickGenerator {
	public static void main(String[] argv) {
		File directory = new File(".");
		new DalliKlickGenerator().generateInDirectory(directory);
	}

	public void generateInDirectory(File directory) {
		if(!directory.isDirectory()) {
			throw new IllegalArgumentException("Expecting a directory to convert the images inside. Have been given " +directory);
		}
		for(File file : directory.listFiles(new ConvertableImagesFilenameFilter())) {
			generateForFile(file);
		}
	}

	public void generateForFile(File file) {
		DalliKlickSequence sequence = new DalliKlickSequence(ImageIO.read(file))
        List<Integer> revealSequence = revealSequenceFromFileName(file.getName())
        List<String> flags = flagsFromFileName(file.getName())
        boolean innerCircle = flags.contains("k")
        boolean addCounter = flags.contains("p")
        List<BufferedImage> images = sequence.generateImages(revealSequence, innerCircle, addCounter);

        images.eachWithIndex {BufferedImage image, Integer i ->
            String fileName = fileNameForSequenceElement(file.getName(), i, revealSequence.max()+(innerCircle ? 1 : 0))
            String format = fileName.substring(fileName.lastIndexOf(".")+1)
            ImageIO.write(image, format, new File(fileName))
        }
	}

	private static class ConvertableImagesFilenameFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.toLowerCase().matches("(?!dalliklick\\.).*(\\.jpg|\\.jpeg|\\.png)");
		}
	}

    private static List<Integer> revealSequenceFromFileName(String fileName) {
        String name = fileName;
        name = name.substring(0, name.lastIndexOf("."));
        if(hasEmbeddedParameters(name)) {
            name = name.substring(name.lastIndexOf(".")+1);
            if(name.length()==0) {
                return 1..12
            }
            if(!name.charAt(0).isDigit()) {
                // strip flags
                int delimiterPos = name.indexOf("-")
                if(delimiterPos==-1) {
                    return 1..12
                }
                name = name.substring(delimiterPos+1);
            }
            return name.split("-").collect {
                try {
                    it.toInteger();
                } catch (NumberFormatException e) {
                }
            }
        } else {
            return 1..12;
        }
    }

    private static List<String> flagsFromFileName(String fileName) {
        String name = fileName;
        name = name.substring(0, name.lastIndexOf("."));
        if(hasEmbeddedParameters(name)) {
            name = name.substring(name.lastIndexOf(".")+1);
            if(name.length()==0) {
                return []
            }
            if(!name.charAt(0).isDigit()) {
                // strip sequence
                int delimiterPos = name.indexOf("-")
                if(delimiterPos!=-1) {
                    name = name.substring(0, delimiterPos);
                }
                return name.split("")
            } else {
                return ["k", "p"]
            }
        } else {
            return ["k", "p"];
        }
    }

    private static boolean hasEmbeddedParameters(String fileName) {
        int separatorIndex = fileName.lastIndexOf(".");
        if(separatorIndex==-1) {
            return false;
        }
        String parameterString = fileName.substring(separatorIndex + 1)
        return parameterString.matches("([kp]{0,2}-)?\\d[-\\d]+\\d") || parameterString.matches("[kp]{0,2}");
    }

    // note: always returns one name more than elements in revealSequence which is the name for the full image
    public static String fileNameForSequenceElement(String fileName, int index, int max) {
        String name = fileName;
        String suffix = name.substring(name.lastIndexOf("."));
        String baseName = name.substring(0, name.lastIndexOf("."));
        if(hasEmbeddedParameters(baseName)) {
            baseName = baseName.substring(0, baseName.lastIndexOf("."));
        }

        "dalliklick.${baseName}.${index.toString().padLeft(max.toString().length(), "0")}${suffix}"
    }
}
