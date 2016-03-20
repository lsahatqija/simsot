package simsot.game;

import java.util.List;

import simsot.framework.Game;
import simsot.framework.Graphics;
import simsot.framework.Graphics.ImageFormat;
import simsot.framework.Input.TouchEvent;
import simsot.framework.Screen;

public class MainMenuScreen extends Screen {
	public MainMenuScreen(Game game) {
		super(game);
	}

	@Override
	public void update(float deltaTime) {
		Graphics g = game.getGraphics();
		Assets.menu= g.newImage("menu.jpg", ImageFormat.RGB565);
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

		int len = touchEvents.size();
		for (int i = 0; i < len; i++) {
			TouchEvent event = touchEvents.get(i);
			if (event.type == TouchEvent.TOUCH_UP) {

				if (inBounds(event, 180, 390, 120, 60)) {
					game.setScreen(new CharacterSelectionScreen(game));
				}

			}
		}
	}

	private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
		return event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1;
	}

	@Override
	public void paint(float deltaTime) {
		Graphics g = game.getGraphics();
		g.drawImage(Assets.menu, 0, 0);
		//g.drawRect(180, 390, 120, 60, Color.WHITE);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void backButton() {
		((SampleGame) game).leaveGame();
	}
}