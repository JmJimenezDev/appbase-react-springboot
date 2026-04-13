package dev.jmjimenez.appbase_rest.util;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

public class ExportUtils {

	public static <T> void exportToCsv(HttpServletResponse response, List<T> data, String[] headers,
			CsvRowMapper<T> mapper, String filename) throws IOException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
		String timestamp = LocalDateTime.now().format(formatter);

		String filenameDate = filename + "_" + timestamp + ".pdf";

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=" + filenameDate);

		PrintWriter writer = response.getWriter();

		writer.println(String.join(";", headers));

		for (T item : data) {
			writer.println(mapper.map(item));
		}

		writer.flush();
		writer.close();
	}

	public static <T> void exportToPdf(HttpServletResponse response, List<T> data, String[] headers,
			PdfRowMapper<T> mapper, String filename) throws DocumentException, IOException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
		String timestamp = LocalDateTime.now().format(formatter);
		String filenameDate = filename + "_" + timestamp + ".pdf";

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=" + filenameDate);

		Document document = new Document(PageSize.A4.rotate());
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();

		PdfPTable table = new PdfPTable(headers.length);
		table.setWidthPercentage(100);

		float[] columnWidths = new float[headers.length];
		for (int i = 0; i < headers.length; i++)
			columnWidths[i] = 2f;
		table.setWidths(columnWidths);

		for (String header : headers) {
			PdfPCell cell = new PdfPCell();
			cell.setBackgroundColor(Color.LIGHT_GRAY);
			cell.setPhrase(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(cell);
		}
		table.setHeaderRows(1);

		for (T item : data) {
			for (String cellData : mapper.map(item)) {
				PdfPCell cell = new PdfPCell(new Phrase(cellData));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				table.addCell(cell);
			}
		}

		document.add(table);
		document.close();
	}

	@FunctionalInterface
	public interface CsvRowMapper<T> {
		String map(T item);
	}

	@FunctionalInterface
	public interface PdfRowMapper<T> {
		String[] map(T item);
	}
}