import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileBack {
    private static final String FILENAME = "KEY_VALUE.txt";
        
        static public void CopyToFile(RedBlackTree.Node root,BufferedWriter bwk)
        {
        try
        {
            if(root!=null)
            {
                CopyToFile(root.left,bwk);
                if(root.key!=0)
                {
                bwk.write(root.key+"->"+root.value);
                bwk.newLine();
                }
                CopyToFile(root.right,bwk);
            }
        }
        catch(Exception e){}
        }
       
	public void get(RedBlackTree.Node root)
        {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = new File(FILENAME);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
                        CopyToFile(root,bw);
		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}

	}
}