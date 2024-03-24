package com.badlogic.drop;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class Drop extends ApplicationAdapter {

	//Assets from Simple Game
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	//Note: Assets are loaded in create() method
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private  Rectangle bucket;
	// differentiate Textures to Sprite Batch and entities (Rectangle)
	private Array<Rectangle> raindrops;
	private long lastDropTime;


	@Override
	public void create () {
		/*
		* Load droplet and bucket images, "64x64" px each
		* Load drop sfx and rain music
		*
		*/

		// TODO: Read into dimensions of images

		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// Start the playback of the background music immediately

		rainMusic.setLooping(true);
		rainMusic.play();
		/*
		* TODO: Check where to play music in different screens
		* TODO: Check how to stop the music of a different screen
		*
		* */
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 400);
		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.width = 64;
		bucket.height = 64;
		bucket.x = 800 / 2 - bucket.getWidth() / 2; //Half of screen - Half of Sprite = center
		bucket.y = 20;


		//Instantiate array to reference for later
		raindrops = new Array<Rectangle>();
		spawnRaindrop();




	}


	/*
	* Textures are loaded in VRAM
	* Internal refers to assets
	* Sound is stored in memory, should be <10s
	* Music is streamed from storage
	*
	* Camera ensure we can render using our target resolution no matter what the actual screen resolution is
	*
	* SpriteBatch is a special class that is used to draw 2D images
	* draw < = >  render
	*
	* */

	@Override
	public void render () {
		ScreenUtils.clear(0,0,0.2f,1);

//		Render camera every time its property is changed
		camera.update();

//		Rendering bucket into screen
		batch.setProjectionMatrix(camera.combined); // Tells spritebatch to use coordinate of camera

//		Include all renders at once to better utilize OpenGL
		batch.begin(); // start request list
		batch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}

		batch.end(); //end request list

//		input.isTouched() checks for mouse clicks

//		input.isKeyPressed(Key) checks for key presses
//		following code moves bucket 200 pixels per second
//		graphics.getDeltaTime() returns the time passed between the last and the current frame in seconds
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

		//Make bucket stay within screen limits

		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - bucket.width) bucket.width = 800 - 64;

		// spawn raindrop based on elapsed time
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

		// make raindrops move down
		for(Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext();) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + raindrop.height < 0) iter.remove();
			if(raindrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
			}

		}
		//In this step, render raindrop, go back to batch begin
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800 - bucket.width);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void dispose() {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}

}
