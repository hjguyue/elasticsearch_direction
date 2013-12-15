package setup;

public class Setup {

	public static void main(String[] args) {
		Property.load();
		
		System.out.println("getting the Mercator ...");
		new GetMercator().getMercator();
		
		System.out.println("building the index ...");
		new BuildIndex().buildIndex();
		
		System.out.println("uploading the data and index ...");
		new Uploader().upload();

		System.out.println("done!");
	}
}
