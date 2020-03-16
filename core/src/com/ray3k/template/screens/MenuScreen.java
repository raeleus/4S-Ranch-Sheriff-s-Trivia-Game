package com.ray3k.template.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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

public class MenuScreen extends JamScreen {
    private Action childrenAction;
    private Action settingsAction;
    private Action parentsAction;
    private Stage stage;
    private Skin skin;
    private Core core;
    private final static Color BG_COLOR = new Color(Color.BLACK);
    
    public MenuScreen(Action childrenAction, Action parentsAction, Action settingsAction) {
        this.childrenAction = childrenAction;
        this.settingsAction = settingsAction;
        this.parentsAction = parentsAction;
    }
    
    @Override
    public void show() {
        core = Core.core;
        skin = core.skin;
        
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
        
        Button button = new Button(skin, "settings");
        root.add(button).expand().right().top().pad(15f);
        button.addListener(core.sndChangeListener);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), settingsAction));
            }
        });
        
        root.row();
        Table table = new Table();
        table.setBackground(skin.getDrawable("panel-dark-ten"));
        root.add(table);
    
        Label label = new Label("San Diego County\nSheriff's Department\nCrime Prevention Trivia Game", skin, "title-bg");
        label.setAlignment(Align.center);
        table.add(label);
        
        table.row();
        Table subTable = new Table();
        table.add(subTable);
        
        subTable.pad(30);
        subTable.defaults().uniform().space(10);
        TextButton textButton = new TextButton("Children", skin);
        subTable.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), childrenAction));
                core.selectRandomChildrenTrivia();
            }
        });
    
        textButton = new TextButton("Parents", skin);
        subTable.add(textButton);
        textButton.addListener(core.sndChangeListener);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.input.setInputProcessor(null);
                fg.addAction(Actions.sequence(Actions.fadeIn(.3f), parentsAction));
                core.selectRandomParentTrivia();
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
