package app.elements;

import app.Icon;
import app.IconManager;
import java.awt.Color;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class IconPicker extends GridPane {

  private final ToggleGroup group;

  public IconPicker(int iconsPerRow) {
    this.group = new ToggleGroup();
    addIcons(IconManager.getAllIcons(), iconsPerRow);
    group
        .selectedToggleProperty()
        .addListener(
            new ChangeListener<Toggle>() {
              @Override
              public void changed(
                  ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue != null) {
                  IconManager.setSelectedIcon((Icon) newValue.getUserData());
                }
              }
            });
  }

  private void addIcons(Icon[] icons, int iconsPerRow) {
    int index = 0;
    for (int i = 0; i < icons.length; i++) {
      VBox entry = makePickerNode(icons[i], i == 0);
      this.add(entry, index % iconsPerRow, i / iconsPerRow);
      index++;
    }
  }

  private VBox makePickerNode(Icon icon, boolean isSelected) {
    VBox vBox = new VBox();
    vBox.setSpacing(15);
    vBox.setAlignment(Pos.CENTER);
    ImageView view = new ImageView(icon.getImage());
    RadioButton radioButton = new RadioButton();
    radioButton.setUserData(icon);
    radioButton.setFocusTraversable(false);
    radioButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000");
    radioButton.setToggleGroup(group);
    // TODO: Necessary? Or already called with listener?
    if (isSelected) {
      radioButton.setSelected(isSelected);
      IconManager.setSelectedIcon(icon);
    }
    vBox.getChildren().add(view);
    vBox.getChildren().add(radioButton);
    return vBox;
  }
}
