package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ray3k.template.Core;
import com.ray3k.template.JamScreen;

public class RandomizerScreen extends JamScreen {
    private Action menuAction;
    private Action questionAction;
    private Stage stage;
    private Skin skin;
    private Core core;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    
    public RandomizerScreen(Action menuAction, Action questionAction) {
        this.menuAction = menuAction;
        this.questionAction = questionAction;
    }
    
    @Override
    public void show() {
        core = Core.core;
        skin = core.skin;
    
        final Music bgm = core.assetManager.get("bgm/music-test.mp3");
        if (!bgm.isPlaying()) {
            bgm.play();
            bgm.setVolume(core.bgm);
            bgm.setLooping(true);
        }
        
        stage = new Stage(new ScreenViewport(), core.batch);
        Gdx.input.setInputProcessor(stage);
    
        Table root = new Table();
        root.setBackground(skin.getDrawable("background-ten"));
        root.setFillParent(true);
        stage.addActor(root);
    
        final Image fg = new Image(skin, "white");
        fg.setColor(Color.BLACK);
        fg.setFillParent(true);
        fg.setTouchable(Touchable.disabled);
        stage.addActor(fg);
        fg.addAction(Actions.sequence(Actions.fadeOut(.3f)));
        
        Button button = new Button(skin, "close");
        root.add(button).expand().right().top().pad(15f);
        button.addListener(core.sndChangeListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), menuAction));
            }
        });
        
        root.row();
        Table table = new Table();
        table.setBackground(skin.getDrawable("panel-dark-ten"));
        root.add(table);
        
        final Sound wheelSound = core.assetManager.get("sfx/prize-wheel.mp3");
        
        table.defaults().space(5);
        
        Stack stack = new Stack();
        table.add(stack);
        
        Image spinnerImage = new Image(skin, "spinner");
        stack.add(spinnerImage);
        spinnerImage.setOrigin(Align.center);
        
        Image image = new Image(skin, "spinner-clicker");
        Container container = new Container(image);
        container.top();
        stack.add(container);
        
        table.row();
        Label label = new Label(core.selectedTrivia.title, skin);
        label.setColor(1, 1, 1, 0);
        table.add(label);
        
        spinnerImage.addAction(Actions.sequence(Actions.delay(.5f), Actions.run(() -> wheelSound.play(core.sfx)), Actions.rotateBy(500, 3f, Interpolation.swingOut), Actions.run(() -> {
            label.addAction(Actions.fadeIn(1f));
        }), Actions.delay(1.5f), Actions.run(() -> {
            Gdx.input.setInputProcessor(null);
            fg.addAction(Actions.sequence(Actions.fadeIn(.3f), questionAction));
        })));
        
        table.row();
        TextButton textButton = new TextButton("Continue", skin);
        table.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                label.setColor(1, 1, 1, 1);
                spinnerImage.clearActions();
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.delay(.5f), Actions.fadeIn(.3f), questionAction));
            }
        });
        
        root.row();
        root.add().expand();
    }
    
    @Override
    public void act(float delta) {
        stage.act(delta);
    }
    
    @Override
    public void draw(float delta) {
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
