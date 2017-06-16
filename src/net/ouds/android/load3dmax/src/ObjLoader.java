package biz.ouds.android.load3dmax;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class ObjLoader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		File file = new File(System.getProperty("user.dir") + "/obj");
		File[] objFiles = file.listFiles();
		
		for (int i = 0; i < objFiles.length; i++) {
			File objFile = objFiles[i]; //new File(System.getProperty("user.dir"), "obj/k.obj");
			
			String fileName = objFile.getName();
			System.out.println("\n\n" + fileName);
			String[] objName = fileName.split("\\.");
			
			if (2 == objName.length && "obj".equalsIgnoreCase(objName[1].trim())) {
			
				ObjModel obj = new ObjModel(objFile);
				float[] vertices = obj.getVertices();
				float[] textures = obj.getTextures();
				float[] normals = obj.getNormals();
				short[] indices = obj.getIndices();
				ObjData data1 = new ObjData();
				data1.setVertices(vertices);
				data1.setTextures(textures);
				data1.setNormals(normals);
				data1.setIndices(indices);
				
				File data = new File(System.getProperty("user.dir"), "obj/ouds/" + objName[0] + ".ouds");
				try {
					ObjectOutputStream objOut = null;
					objOut = new ObjectOutputStream(new BufferedOutputStream(
							new FileOutputStream(data)));
					objOut.writeObject(data1);
					objOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
