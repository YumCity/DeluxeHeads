package nl.marido.deluxeheads.menu;

import org.bukkit.Material;

import nl.marido.deluxeheads.menu.ui.Bounds;
import nl.marido.deluxeheads.menu.ui.MenuResponse;
import nl.marido.deluxeheads.menu.ui.Position;
import nl.marido.deluxeheads.menu.ui.element.Container;
import nl.marido.deluxeheads.menu.ui.element.Element;
import nl.marido.deluxeheads.menu.ui.item.Button;
import nl.marido.deluxeheads.menu.ui.item.Item;
import nl.marido.deluxeheads.util.Checks;
import nl.marido.deluxeheads.util.SafeCall;
import nl.marido.deluxeheads.util.Stringify;
import nl.marido.deluxeheads.volatilecode.Items;

import java.util.concurrent.Callable;

public class ConfirmationMenu extends Element {

    public static final Item defaultAccept = Items.createGreenStainedClay().name("&aAccept");
    public static final Item defaultDecline = Items.createRedStainedClay().name("&cDecline");
    public static final Button defaultSubject = Item.create(Material.AIR).buildButton();

    public static final Template defaultTemplate = new Template(defaultAccept, defaultDecline);

    private Template template;

    private final Callable<MenuResponse> onAccept;
    private final Callable<MenuResponse> onDecline;

    private Button subject;

    public ConfirmationMenu(Bounds bounds, Callable<MenuResponse> onAccept, Callable<MenuResponse> onDecline) {
        super(bounds);

        Checks.ensureNonNull(onAccept, "onAccept");
        Checks.ensureNonNull(onDecline, "onDecline");
        Checks.ensureTrue(bounds.width >= 3, "bounds must have a width of at least 3");
        Checks.ensureTrue(bounds.width >= 2, "bounds must have a height of at least 2");

        this.onAccept = SafeCall.nonNullCallable(onAccept, "onAccept");
        this.onDecline = SafeCall.nonNullCallable(onDecline, "onDecline");

        setTemplate(defaultTemplate, defaultSubject);
    }

    @Override
    public Button[] getItems() {
        Container container = new Container(bounds);

        Position subjectPosition = new Position(bounds.width / 2,     (bounds.height - 1) / 3);
        Position acceptPosition  = new Position(bounds.width / 3,     (bounds.height - 1) * 2 / 3);
        Position declinePosition = new Position(bounds.width * 2 / 3, (bounds.height - 1) * 2 / 3);

        container.setItem(subjectPosition, subject);
        container.setItem(acceptPosition, template.constructAccept(this));
        container.setItem(declinePosition, template.constructDecline(this));

        return container.getItems();
    }

    public void setTemplate(Template template, Button subject) {
        Checks.ensureNonNull(template, "template");

        this.template = template;
        this.subject = subject;
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("template", template)
                .entry("subject", subject)
                .entry("onAccept", onAccept)
                .entry("onDecline", onDecline).toString();
    }

    public static final class Template {

        private final Item accept;
        private final Item decline;

        public Template(Item accept, Item decline) {
            Checks.ensureNonNull(accept, "accept");
            Checks.ensureNonNull(decline, "decline");

            this.accept = accept;
            this.decline = decline;
        }

        public Button constructAccept(ConfirmationMenu menu) {
            return accept.buildButton(menu.onAccept);
        }

        public Button constructDecline(ConfirmationMenu menu) {
            return accept.buildButton(menu.onDecline);
        }

        @Override
        public String toString() {
            return Stringify.builder()
                    .entry("accept", accept)
                    .entry("decline", decline).toString();
        }

    }

}
