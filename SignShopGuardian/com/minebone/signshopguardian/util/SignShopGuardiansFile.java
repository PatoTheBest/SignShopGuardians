package com.minebone.signshopguardian.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;
import com.minebone.signshopguardian.SignShopGuardian;

public abstract class SignShopGuardiansFile {
	
	protected SignShopGuardian plugin;
	protected FileConfiguration guardiansConfig;
	protected File guardiansFile;
	protected String fileName;
	protected boolean hasLoaded;
	
	public SignShopGuardiansFile(SignShopGuardian plugin, String fileName) {
		this.fileName = fileName;
		this.plugin = plugin;
		this.hasLoaded = false;
	}

	public abstract void load();

	public SignShopGuardiansFile reload() {
		if (guardiansFile == null) {
			guardiansFile = new File("plugins/SignShopGuardian/" + fileName + ".yml");
		}	
		if(!guardiansFile.exists()) {
			if(!hasLoaded) {
				generateFile();
				hasLoaded = true;
			}
		}
		YamlConfiguration lang1 = new YamlConfiguration();
		
		try {
			FileInputStream stream = new FileInputStream(new File("plugins/SignShopGuardian/" + fileName + ".yml"));
			InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
			BufferedReader input = new BufferedReader(reader);
			
            int commentNum = 0;
            
            String addLine;
            String currentLine;
 
            StringBuilder whole = new StringBuilder("");
 
            while((currentLine = input.readLine()) != null) {
 
                if(currentLine.startsWith("#")) {
                    addLine = currentLine.replace(":", "REPLACED_COLON").replace("{", "RIGHT_BRACKET").replace("}", "LEFT_BRACKET").replace("(", "RIGHT_PARENTESIS").replace(")", "LEFT_PARENTESIS").replaceFirst("#", "THETOWERS_COMMENT_" + commentNum + ":").replace(": ", ": '").replace(":#", ": '#");
                    whole.append(addLine + "'\n");
                    commentNum++;
 
                } else {
                    whole.append(currentLine + "\n");
                }
 
            }
            
			input.close();

			lang1.loadFromString(whole.toString());
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InvalidConfigurationException e1) {
			e1.printStackTrace();
		}
	       guardiansConfig = lang1;
	       customLoad();
	       return this;
	}

	protected void generateFile() {
		load();
	}

	public FileConfiguration getYamlConfiguration() {
		if (guardiansConfig == null) {
			reload();
		}
		return guardiansConfig;
	}

	public void save() {
		if (guardiansConfig == null || guardiansFile == null) {
			return;
		}
		try {
			Files.createParentDirs(guardiansFile);
			String data = prepareConfigString(guardiansConfig.saveToString());

			Writer writer = new OutputStreamWriter(new FileOutputStream(guardiansFile), Charset.forName("UTF-8"));
			try {
				writer.write(data);
			} finally {
				writer.close();
			}
		} catch (IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE,
					"Could not save " + fileName + " to " + guardiansFile, ex);
		}
	}
	
	public void customLoad() {
		
	}
	
	public boolean isYamlNull() {
		return guardiansConfig == null;
	}
	
	private String prepareConfigString(String configString) {
		 
        int lastLine = 0;
        int headerLine = 0;
 
        String[] lines = configString.split("\n");
        StringBuilder config = new StringBuilder("");
        for(String line : lines) {
            if(line.startsWith("THETOWERS_COMMENT")) {
                String comment = "#" + line.trim().substring(line.indexOf(":") + 1);
                
                if(comment.startsWith("# +-")) {
 
                    /*
                    * If header line = 0 then it is
                    * header start, if it's equal
                    * to 1 it's the end of header
                    */
 
                    if(headerLine == 0) {
                        config.append(comment + "\n");
 
                        lastLine = 0;
                        headerLine = 1;
 
                    } else if(headerLine == 1) {
                        config.append(comment + "\n\n");
 
                        lastLine = 0;
                        headerLine = 0;
 
                    }
 
                } else {
 
                    /*
                    * Last line = 0 - Comment
                    * Last line = 1 - Normal path
                    */
 
                    String normalComment;
 
                    if(comment.startsWith("# '")) {
                        normalComment = comment.substring(0, comment.length() - 1).replaceFirst("# '#", "##").replaceFirst("# '", "# ").replace("REPLACED_COLON", ":");
                    } else {
                        normalComment = comment.replace("REPLACED_COLON", ":").replace("RIGHT_BRACKET", "{").replace("LEFT_BRACKET", "}").replace("RIGHT_PARENTESIS", "(").replace("LEFT_PARENTESIS", ")");
                    }
 
                    if(lastLine == 0) {
                        config.append(normalComment + "\n");
                    } else if(lastLine == 1) {
                        config.append("\n" + normalComment + "\n");
                    }
 
                    lastLine = 0;
 
                }
 
            } else {
                config.append(line + "\n");
                lastLine = 1;
            }
 
        }
 
        return config.toString();
 
    }
    
    protected String getHeader(String title) {
    	StringBuilder header = new StringBuilder("");
	    header.append("############################################################\n");
	    header.append("# +------------------------------------------------------+ #\n");
	    header.append("# |" + StringUtils.center(title,54) + "| #\n");
	    header.append("# +------------------------------------------------------+ #\n");
	    header.append("############################################################\n");
	    return header.toString();
    }
}
class StringUtils {

    public static String center(String s, int size) {
        return center(s, size, " ");
    }

    public static String center(String s, int size, String pad) {
        if (pad == null)
            throw new NullPointerException("pad cannot be null");
        if (pad.length() <= 0)
            throw new IllegalArgumentException("pad cannot be empty");
        if (s == null || size <= s.length())
            return s;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }
}
