package hn.testuth.views.asignaturas;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import hn.testuth.data.Asignatura;
import hn.testuth.services.AsignaturaService;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Asignaturas")
@Route("asignaturas/:asignaturaID?/:action?(edit)")
@Menu(order = 1, icon = "line-awesome/svg/book-solid.svg")
public class AsignaturasView extends Div implements BeforeEnterObserver {

    private final String ASIGNATURA_ID = "asignaturaID";
    private final String ASIGNATURA_EDIT_ROUTE_TEMPLATE = "asignaturas/%s/edit";

    private final Grid<Asignatura> grid = new Grid<>(Asignatura.class, false);

    private TextField nombre;
    private TextField catedratico;
    private TextField horario;
    private TextField modalidad;
    private TextField precio;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Asignatura> binder;

    private Asignatura asignatura;

    private final AsignaturaService asignaturaService;

    public AsignaturasView(AsignaturaService asignaturaService) {
        this.asignaturaService = asignaturaService;
        addClassNames("asignaturas-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("catedratico").setAutoWidth(true);
        grid.addColumn("horario").setAutoWidth(true);
        grid.addColumn("modalidad").setAutoWidth(true);
        grid.addColumn("precio").setAutoWidth(true);
        grid.setItems(query -> asignaturaService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ASIGNATURA_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AsignaturasView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Asignatura.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(precio).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("precio");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.asignatura == null) {
                    this.asignatura = new Asignatura();
                }
                binder.writeBean(this.asignatura);
                asignaturaService.update(this.asignatura);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(AsignaturasView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> asignaturaId = event.getRouteParameters().get(ASIGNATURA_ID).map(Long::parseLong);
        if (asignaturaId.isPresent()) {
            Optional<Asignatura> asignaturaFromBackend = asignaturaService.get(asignaturaId.get());
            if (asignaturaFromBackend.isPresent()) {
                populateForm(asignaturaFromBackend.get());
            } else {
                Notification.show(String.format("The requested asignatura was not found, ID = %s", asignaturaId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AsignaturasView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        nombre = new TextField("Nombre");
        catedratico = new TextField("Catedratico");
        horario = new TextField("Horario");
        modalidad = new TextField("Modalidad");
        precio = new TextField("Precio");
        formLayout.add(nombre, catedratico, horario, modalidad, precio);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Asignatura value) {
        this.asignatura = value;
        binder.readBean(this.asignatura);

    }
}
