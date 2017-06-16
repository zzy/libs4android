package biz.ouds.android.load3dmax;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class ObjModel {
	private float[] vertices;
	private float[] textures;
	private float[] normals;
	private short[] indices;

	public ObjModel(File data) {
		this.loadData(data);
	}

	public void loadData(File data) {
		InputStream input;
		BufferedReader reader;
		try {
			ArrayList<String> verticeLines = new ArrayList<String>();
			ArrayList<String> textureLines = new ArrayList<String>();
			ArrayList<String> normalLines = new ArrayList<String>();
			ArrayList<String> verticeIndiceLines = new ArrayList<String>();
			ArrayList<String> textureIndiceLines = new ArrayList<String>();
			ArrayList<String> normalIndiceLines = new ArrayList<String>();
			input = new FileInputStream(data);
			reader = new BufferedReader(new InputStreamReader(input));
			String line = null;
			while ((line = reader.readLine()) != null) {
				
				if (line.startsWith("//") || line.startsWith("#") || line.trim().equals("")) {
					continue;
				}
				String SPACE = " ";
				String SLASH = "/";
				StringTokenizer st = new StringTokenizer(line, SPACE);
				String lineType = st.nextToken();
				System.out.println("lineType:" + lineType);
				
				if (lineType.equals("v")) {
					verticeLines.add(st.nextToken());
					verticeLines.add(st.nextToken());
					verticeLines.add(st.nextToken());
				}
				else if (lineType.equals("vt")) {
					textureLines.add(st.nextToken());
					textureLines.add(st.nextToken());
				}
				else if (lineType.equals("vn")) {
					normalLines.add(st.nextToken());
					normalLines.add(st.nextToken());
					normalLines.add(st.nextToken());
				}
				else if (lineType.equals("f")) {
					System.out.print(line);
												
					String v1 = st.nextToken();
					String v2 = st.nextToken();
					String v3 = st.nextToken();

					StringTokenizer st1 = new StringTokenizer(v1, SLASH);
					StringTokenizer st2 = new StringTokenizer(v2, SLASH);
					StringTokenizer st3 = new StringTokenizer(v3, SLASH);

					verticeIndiceLines.add(st1.nextToken());
					verticeIndiceLines.add(st2.nextToken());
					verticeIndiceLines.add(st3.nextToken());

					textureIndiceLines.add(st1.nextToken());
					textureIndiceLines.add(st2.nextToken());
					textureIndiceLines.add(st3.nextToken());

					normalIndiceLines.add(st1.nextToken());
					normalIndiceLines.add(st2.nextToken());
					normalIndiceLines.add(st3.nextToken());
				}
			}
			// System.out.println("textureLines:"+textureLines.toString());
			// System.out.println("textureIndiceLines:"+textureIndiceLines.toString());

			int indicesSize = verticeIndiceLines.size();
			indices = new short[indicesSize];
			int verticeSize = indicesSize * 3;
			vertices = new float[verticeSize];

			int textureSize = indicesSize * 2;
			textures = new float[textureSize];

			int normalSize = indicesSize * 3;
			normals = new float[normalSize];
			
			for (int i = 0; i < verticeIndiceLines.size(); i++) {

				indices[i] = (short) i;
				int indice = Integer.valueOf(verticeIndiceLines.get(i)) - 1;

				vertices[i * 3] = Float.valueOf(verticeLines.get(indice * 3));
				vertices[i * 3 + 1] = Float.valueOf(verticeLines
						.get(indice * 3 + 1));
				vertices[i * 3 + 2] = Float.valueOf(verticeLines
						.get(indice * 3 + 2));


				int textureIndice = Integer.valueOf(textureIndiceLines.get(i)) - 1;
				textures[i * 2] = Float.valueOf(textureLines
						.get(textureIndice * 2));
				textures[i * 2 + 1] = Float.valueOf(textureLines
						.get(textureIndice * 2 + 1));


				int normalIndice = Integer.valueOf(normalIndiceLines.get(i)) - 1;
				normals[i * 3] = Float.valueOf(normalLines
						.get(normalIndice * 3));
				normals[i * 3 + 1] = Float.valueOf(normalLines
						.get(normalIndice * 3 + 1));
				normals[i * 3 + 2] = Float.valueOf(normalLines
						.get(normalIndice * 3 + 2));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public float[] getVertices() {
		System.out.println("vertices:" + Arrays.toString(vertices));
		return vertices;
	}

	public float[] getTextures() {
		System.out.println("textures:" + Arrays.toString(textures));
		return textures;
	}

	public float[] getNormals() {
		System.out.println("normals:" + Arrays.toString(normals));
		return normals;
	}

	public short[] getIndices() {
		System.out.println("indices:" + Arrays.toString(indices));
		return indices;
	}

}
