package gaia.cu9.ari.gaiaorbit.util.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import gaia.cu9.ari.gaiaorbit.util.GlobalResources;

/**
 * ImageButton in which the cursor changes when the mouse rolls over.
 * @author Toni Sagrista
 *
 */
public class OwnImageButton extends ImageButton {
    Array<EventListener> listeners;
    OwnImageButton me;

    public OwnImageButton(Skin skin) {
        super(skin);
        this.me = this;
        initialize();
    }

    public OwnImageButton(Skin skin, String styleName) {
        super(skin, styleName);
        this.me = this;
        initialize();
    }

    public OwnImageButton(ImageButtonStyle style) {
        super(style);
        this.me = this;
        initialize();
    }

    public void setCheckedNoFire(boolean isChecked) {
        // Remove listeners

        for (EventListener listener : this.getListeners()) {
            listeners.add(listener);
        }
        this.clearListeners();
        // Check
        this.setChecked(isChecked);
        // Add listeners
        for (EventListener listener : listeners) {
            this.addListener(listener);
        }
        listeners.clear();
    }

    private void initialize() {
        listeners = new DelayedRemovalArray();
        this.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof InputEvent) {
                    Type type = ((InputEvent) event).getType();
                    if (type == Type.enter) {
                        if (!me.isDisabled())
                            Gdx.input.setCursorImage(GlobalResources.linkCursor, 4, 0);
                        return true;
                    } else if (type == Type.exit) {
                        Gdx.input.setCursorImage(null, 0, 0);
                        return true;
                    }

                }
                return false;
            }
        });
    }

}
