import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int LarguraBorda = 360;
    int AlturaBorda = 640;

    Image birdImage;
    Image backgroundImage;
    Image bottomPipeImage;
    Image topPipeImage;

    int birdX = LarguraBorda / 8;
    int birdY = AlturaBorda / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    int pipeX = LarguraBorda;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    Bird bird;
    ArrayList<Pipe> pipes;

    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;

    double counter = 0;

    FlappyBird() {

        setPreferredSize(new Dimension(LarguraBorda, AlturaBorda));
        setFocusable(true);
        addKeyListener(this);

        backgroundImage = new ImageIcon(getClass().getResource("/flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("/flappybird.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("/toppipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("/bottompipe.png")).getImage();

        bird = new Bird(birdImage);
        pipes = new ArrayList<>();

        placePipesTimer = new Timer(1500, e -> placePipes());
        placePipesTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void placePipes() {

        int randomPipeY =
                (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));

        int openingSpace = AlturaBorda / 4;

        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        g.drawImage(backgroundImage, 0, 0,
                LarguraBorda, AlturaBorda, null);

        g.drawImage(bird.img,
                bird.x,
                bird.y,
                bird.width,
                bird.height,
                null);

        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img,
                    pipe.x,
                    pipe.y,
                    pipe.width,
                    pipe.height,
                    null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + (int) counter, 10, 30);
    }

    public void move() {

        velocityY += gravity;
        bird.y += velocityY;

        if (bird.y < 0) {
            bird.y = 0;
        }

        for (Pipe pipe : pipes) {

            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                counter += 0.5;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > AlturaBorda) {
            gameOver = true;
        }

        for (int i = 0; i < pipes.size(); i++) {

    Pipe pipe = pipes.get(i);
    pipe.x += velocityX;

    if (pipe.x + pipe.width < 0) {
        pipes.remove(i);
        i--;
    }

    if (!pipe.passed && bird.x > pipe.x + pipe.width) {
        pipe.passed = true;
        counter += 0.5;
    }

    if (collision(bird, pipe)) {
        gameOver = true;
    }
}
    }

    public boolean collision(Bird a, Pipe b) {

        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        move();
        repaint();

        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

            velocityY = -9;

            if (gameOver) {

                bird.x = birdX;
                bird.y = birdY;
                velocityY = 0;

                pipes.clear();

                counter = 0;
                gameOver = false;

                placePipesTimer.start();
                gameLoop.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}