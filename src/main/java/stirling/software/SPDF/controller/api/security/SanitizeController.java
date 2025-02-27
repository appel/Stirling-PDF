package stirling.software.SPDF.controller.api.security;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.interactive.action.*;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import stirling.software.SPDF.utils.WebResponseUtils;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class SanitizeController {

	@PostMapping(consumes = "multipart/form-data", value = "/sanitize-pdf")
	@Operation(summary = "Sanitize a PDF file",
	        description = "This endpoint processes a PDF file and removes specific elements based on the provided options. Input:PDF Output:PDF Type:SISO")
	public ResponseEntity<byte[]> sanitizePDF(
	        @RequestPart(required = true, value = "fileInput")
	        @Parameter(description = "The input PDF file to be sanitized")
	                MultipartFile inputFile,
	        @RequestParam(name = "removeJavaScript", required = false, defaultValue = "false")
	        @Parameter(description = "Remove JavaScript actions from the PDF if set to true")
	                Boolean removeJavaScript,
	        @RequestParam(name = "removeEmbeddedFiles", required = false, defaultValue = "false")
	        @Parameter(description = "Remove embedded files from the PDF if set to true")
	                Boolean removeEmbeddedFiles,
	        @RequestParam(name = "removeMetadata", required = false, defaultValue = "false")
	        @Parameter(description = "Remove metadata from the PDF if set to true")
	                Boolean removeMetadata,
	        @RequestParam(name = "removeLinks", required = false, defaultValue = "false")
	        @Parameter(description = "Remove links from the PDF if set to true")
	                Boolean removeLinks,
	        @RequestParam(name = "removeFonts", required = false, defaultValue = "false")
	        @Parameter(description = "Remove fonts from the PDF if set to true")
	                Boolean removeFonts) throws IOException {

	    try (PDDocument document = PDDocument.load(inputFile.getInputStream())) {
	        if (removeJavaScript) {
	            sanitizeJavaScript(document);
	        }

	        if (removeEmbeddedFiles) {
	            sanitizeEmbeddedFiles(document);
	        }

	        if (removeMetadata) {
	            sanitizeMetadata(document);
	        }

	        if (removeLinks) {
	            sanitizeLinks(document);
	        }

	        if (removeFonts) {
	            sanitizeFonts(document);
	        }

	        return WebResponseUtils.pdfDocToWebResponse(document, inputFile.getOriginalFilename().replaceFirst("[.][^.]+$", "") + "_sanitized.pdf");
	    }
	}
	private void sanitizeJavaScript(PDDocument document) throws IOException {
		// Get the root dictionary (catalog) of the PDF
	    PDDocumentCatalog catalog = document.getDocumentCatalog();

	    // Get the Names dictionary
	    COSDictionary namesDict = (COSDictionary) catalog.getCOSObject().getDictionaryObject(COSName.NAMES);

	    if (namesDict != null) {
	        // Get the JavaScript dictionary
	        COSDictionary javaScriptDict = (COSDictionary) namesDict.getDictionaryObject(COSName.getPDFName("JavaScript"));

	        if (javaScriptDict != null) {
	            // Remove the JavaScript dictionary
	            namesDict.removeItem(COSName.getPDFName("JavaScript"));
	        }
	    }
	    
	    for (PDPage page : document.getPages()) {
            for (PDAnnotation annotation : page.getAnnotations()) {
                if (annotation instanceof PDAnnotationWidget) {
                    PDAnnotationWidget widget = (PDAnnotationWidget) annotation;
                    PDAction action = widget.getAction();
                    if (action instanceof PDActionJavaScript) {
                        widget.setAction(null);
                    }
				}
			}
	        PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
	        if (acroForm != null) {
	            for (PDField field : acroForm.getFields()) {
	            	PDFormFieldAdditionalActions actions = field.getActions();
	            	if(actions != null) {
	            		if (actions.getC() instanceof PDActionJavaScript) {
	                        actions.setC(null);
	                    }
	                    if (actions.getF() instanceof PDActionJavaScript) {
	                        actions.setF(null);
	                    }
	                    if (actions.getK() instanceof PDActionJavaScript) {
	                        actions.setK(null);
	                    }
	                    if (actions.getV() instanceof PDActionJavaScript) {
	                        actions.setV(null);
	                    }
	            	}
	            }
	        }
	    }
	}




    private void sanitizeEmbeddedFiles(PDDocument document) {
        PDPageTree allPages = document.getPages();

        for (PDPage page : allPages) {
            PDResources res = page.getResources();

            // Remove embedded files from the PDF
            res.getCOSObject().removeItem(COSName.getPDFName("EmbeddedFiles"));
        }
    }
    

    private void sanitizeMetadata(PDDocument document) {
        PDMetadata metadata = document.getDocumentCatalog().getMetadata();
        if (metadata != null) {
            document.getDocumentCatalog().setMetadata(null);
        }
    }



    private void sanitizeLinks(PDDocument document) throws IOException {
        for (PDPage page : document.getPages()) {
            for (PDAnnotation annotation : page.getAnnotations()) {
                if (annotation instanceof PDAnnotationLink) {
                    PDAction action = ((PDAnnotationLink) annotation).getAction();
                    if (action instanceof PDActionLaunch || action instanceof PDActionURI) {
                        ((PDAnnotationLink) annotation).setAction(null);
                    }
                }
            }
        }
    }

    private void sanitizeFonts(PDDocument document) {
        for (PDPage page : document.getPages()) {
            page.getResources().getCOSObject().removeItem(COSName.getPDFName("Font"));
        }
    }
    
}
