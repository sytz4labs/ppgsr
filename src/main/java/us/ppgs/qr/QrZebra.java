package us.ppgs.qr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/qrs")
public class QrZebra {

	@GetMapping("")
	public String indexb() {
		return "redirect:/qrs/";
	}

	@GetMapping("/")
	public String indexSlash() {
		return "/html/qrs.html";
	}

	private int num = 0;
	
	@GetMapping("/test.png")
	@ResponseBody
	public void test(HttpServletResponse res) throws WriterException, IOException {

		var nonce = num++;
		
		byte[] testMsg = new byte[375];
		for (int i=0; i<testMsg.length; i++) {
			testMsg[i] = (byte) (i + nonce);
		}
		
		var testMsg64 = "TeSt" + new String(Base64.getEncoder().encode(testMsg));
		
		var fileType = "png";
		var size = 1000;
		
		res.setContentType("image/" + fileType);
		createQRImage(res.getOutputStream(), testMsg64, size, fileType);
	}

//	@GetMapping("")
//	@ResponseBody
//	public String index() throws WriterException, IOException {
//
//		String qrCodeText = "2023-09-28T11:50:59.737-05:00  WARN  --- [  restartedMain] ocalVariableTableParameterNameDiscoverer ";
//		
//		StringBuilder sb = new StringBuilder();
//		for (int i=1; i<=5; i++) {
//			sb.append(qrCodeText);
//		}
//		for (int i=1; i<=15; i++) {
//			var fileType = "png";
//			var size = 1000;
//			String filePath = "JD" + i + ".png";
//			File qrFile = new File(filePath);
//			
//			createQRImage(qrFile, sb.toString(), size, fileType);
//			//createPDF417(qrFile, sb.toString());
//		}
//		
//		return "OK " + System.currentTimeMillis();
//	}
	
//	@GetMapping("/x")
//	@ResponseBody
//	public String x() {
//		var args = new String[] { "/my/jd15.png"};
//		try {
//			CommandLineRunner.main(args);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		return "OK " + System.currentTimeMillis();
//	}
//	
	private void createQRImage(OutputStream qrFile, String qrCodeText, int size, String fileType) throws WriterException, IOException {
		// Create the ByteMatrix for the QR-Code that encodes the given String
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size * 2, size, hintMap);
		// Make the BufferedImage that are to hold the QRCode
		int matrixWidth = byteMatrix.getWidth();
		int matrixHeight = byteMatrix.getHeight();
		BufferedImage image = new BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		System.out.println(" " + byteMatrix.getWidth() + " x " + byteMatrix.getHeight() + " l =  " + qrCodeText.length());

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixHeight);
		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixHeight; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		ImageIO.write(image, fileType, qrFile);
	}
}
