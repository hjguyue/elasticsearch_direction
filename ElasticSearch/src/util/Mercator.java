package util;

public class Mercator {

	private static double M_PI = Math.PI;

	// 经纬度转墨卡托
	// 经度(lon)，纬度(lat)
	public static double[] lonLat2Mercator(double lon, double lat) {
		double[] xy = new double[2];
		double x = lon * 20037508.342789 / 180;
		double y = Math.log(Math.tan((90 + lat) * M_PI / 360)) / (M_PI / 180);
		y = y * 20037508.34789 / 180;
		xy[0] = x;
		xy[1] = y;
		return xy;
	}

	// 墨卡托转经纬度
	public static double[] Mercator2lonLat(double mercatorX, double mercatorY) {
		double[] xy = new double[2];
		double x = mercatorX / 20037508.34 * 180;
		double y = mercatorY / 20037508.34 * 180;
		y = 180 / M_PI * (2 * Math.atan(Math.exp(y * M_PI / 180)) - M_PI / 2);
		xy[0] = x;
		xy[1] = y;
		return xy;
	}

	public static void main(String argv[]) {
		System.out.println(lonLat2Mercator( -74.014245, 40.791636)[0] + ", "
				+ lonLat2Mercator(-74.014245, 40.791636)[1]);
		
		System.out.println(Mercator2lonLat(-2.0037508342789E7, 2.0037508342789E7)[0] + ", " 
		+ Mercator2lonLat(-2.0037508342789E7, 2.0037508342789E7)[1]);
	}
}
