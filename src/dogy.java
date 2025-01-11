import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dogy {
    JFrame frame;
    JLabel imageViewer;
    JButton fetchButton;

    public dogy(){
        frame = new JFrame("DOG viewer app");
        imageViewer = new JLabel("", SwingConstants.CENTER);
        fetchButton = new JButton("GET A DOG");

        initUI();
    }

    public void initUI(){
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout()); //макет
        frame.add(imageViewer, BorderLayout.CENTER);

        fetchButton.setFont(new Font("Arial", Font.ITALIC, 20));
        fetchButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dogurl = "https://dog.ceo/api/breeds/image/random";

                try {
                    URL url = new URL(dogurl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    InputStream inputStream = conn.getInputStream();
                    StringBuilder content = new StringBuilder();
                    int ch;
                    while ((ch = inputStream.read()) != -1) {
                        content.append((char) ch);
                    }

                    inputStream.close();
                    conn.disconnect();

                    // Извлечение URL изображения из JSON-ответа
                    String imageUrl = extractImageUrlFromJson(content.toString());
                    displayImage(imageUrl);

                } catch (MalformedURLException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JPanel jPanel = new JPanel(); //регулирование расположения кнопки (месо под кнопку)
        jPanel.setLayout(new FlowLayout());
        jPanel.add(fetchButton);
        frame.add(jPanel, BorderLayout.SOUTH);


        frame.setVisible(true); //олвэйс последним
    }

    private String extractImageUrlFromJson(String jsonResponse) {
        Pattern pattern = Pattern.compile("\"message\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(jsonResponse);
        if (matcher.find()) {
            return matcher.group(1).replace("\\", "");
        }
        throw new IllegalArgumentException("Invalid JSON response");
    }


    private void displayImage(String imageUrl){
        try{
            URL url = new URL(imageUrl);
            ImageIcon imageIcon = new ImageIcon(url);
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
            ImageIcon dogImage = new ImageIcon(scaledImage);

            imageViewer.setIcon(dogImage);
            imageViewer.repaint();


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }



    public static void main(String[] args){

        SwingUtilities.invokeLater(dogy::new);

    }
}