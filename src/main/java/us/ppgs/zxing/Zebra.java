package us.ppgs.zxing;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.CommandLineRunner;
//import com.google.zxing.client.j2se.DecoderConfig;
import com.google.zxing.client.j2se.ImageReader;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.common.HybridBinarizer;

@Controller
public class Zebra {

	@GetMapping("/zxing/x")
	@ResponseBody
	public String x() throws URISyntaxException, IOException {
		var args = new String[] { "2124_17.298.jpg" };
		
		try {
			CommandLineRunner.main(args);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "OK" + System.currentTimeMillis();
	}

	@GetMapping("/zxing/y")
	@ResponseBody
	public String y() throws URISyntaxException, IOException {

		URI uri = Paths.get(new URI("2124_17.298.jpg").getRawPath()).toUri();

		DecoderConfig config = new DecoderConfig();
		Map<DecodeHintType, ?> hints = config.buildHints();

		try {
			BufferedImage image = ImageReader.readImage(uri);

			LuminanceSource source = new BufferedImageLuminanceSource(image);

			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			MultiFormatReader multiFormatReader = new MultiFormatReader();
			Result result;
			try {
				result = multiFormatReader.decode(bitmap, hints);
			}
			catch (NotFoundException ignored) {
				System.out.println(uri + ": No barcode found");
				return null;
			}

			StringWriter output = new StringWriter();
			ParsedResult parsedResult = ResultParser.parseResult(result);
			output.write(uri + " (format: " + result.getBarcodeFormat() + ", type: " + parsedResult.getType()
					+ "):\n" + "Raw result:\n" + result.getText() + "\n");

			System.out.println(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "OK" + System.currentTimeMillis();
	}
}
