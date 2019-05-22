package my.vaadin.app;

import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.SketchCanvas;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    private SketchCanvas sketchcanvas = new SketchCanvas();
    private SketchCanvas sketchcanvas2 = new SketchCanvas();
    private Button downloadAsPNG = new Button("Download as PNG");
    private Button showSecondCanvas = new Button("Show Second Canvas");
    private Button addNewCanvas = new Button("Add New Canvas");
    private Button setBackground = new Button("Set Background");
    private TextField backgroundSource = new TextField();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final VerticalLayout layout = new VerticalLayout();

        sketchcanvas.setWidth("500px");
        sketchcanvas.setHeight("500px");
        sketchcanvas.setCanvasWidth(500);
        sketchcanvas.setCanvasHeight(500);
        HorizontalLayout canvas = new HorizontalLayout(sketchcanvas);
        layout.addComponent(canvas);
        HorizontalLayout groupButtons = new HorizontalLayout();
        groupButtons.addComponent(downloadAsPNG);
        groupButtons.addComponent(showSecondCanvas);
        groupButtons.addComponent(setBackground);
        groupButtons.addComponent(backgroundSource);
//        groupButtons.addComponent(addNewCanvas);
        layout.addComponent(groupButtons);

        setBackground.addClickListener(e -> {
            sketchcanvas.setBackgroundImage(backgroundSource.getValue());
        });
        backgroundSource.addValueChangeListener(e -> {
            setBackground.setEnabled(e.getValue()!=null && !e.getValue().isEmpty());
        });
        setBackground.setEnabled(false);

        downloadAsPNG.addClickListener(e -> {
            sketchcanvas.requestImageAsBase64(img -> {
                FileDownloader fd = new FileDownloader(
                        SketchCanvas.getPNGResource(img, "my_image.png"));
                Button downloadButton = new Button("download");
                downloadButton.addStyleName("tiny");
                fd.extend(downloadButton);
                layout.addComponent(downloadButton);
            });
        });

        showSecondCanvas.addClickListener(e -> {
            sketchcanvas2.setWidth("500px");
            sketchcanvas2.setHeight("500px");
            sketchcanvas2.setCanvasHeight(500);
            sketchcanvas.setCanvasWidth(500);
            canvas.addComponent(sketchcanvas2);

            sketchcanvas.addDrawingChangeListener(json -> {
                sketchcanvas2.updateDrawing(json);
            });

            sketchcanvas2.addDrawingChangeListener(json -> {
                sketchcanvas.updateDrawing(json);
            });
        });

//        addNewCanvas.addClickListener(e -> {
//
//        });

        setContent(layout);
    }


    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
