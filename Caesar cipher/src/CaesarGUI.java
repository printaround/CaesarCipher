import javax.swing.*;
import java.awt.*;
import java.io.*;

public class CaesarGUI extends JFrame{

    private JPanel mainPanel;
    private JButton encryptionBtn;
    private JButton decryptionBtn;
    private JButton enumerationMethodBtn;
    private JButton frequencyBtn;
    private JButton saveBtn;
    private JButton uploadBtn;

    private final JTextArea inputText;
    private final JTextArea outputText;
    private final JTextArea shiftValueText;

    private static final String HighRusAlphabet = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final String LowRusAlphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private static final String HighEngAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LowEngAlphabet = "abcdefghijklmnopqrstuvwxyz";
    private static final String FrequencyHighRusAlphabet = "ОЕАИНТСРВЛКМДПУЯГЗБЧЙХЖШЮЦЩЭФЁЫЬЪ";
    private static final String FrequencyLowRusAlphabet = "оеаинтсрвлкмдпуяыьгзбчйхжшюцщэфъё";
    private static final String FrequencyHighEngAlphabet = "EARIOTNSLCUDPMHGBFYWKVXJQZ";
    private static final String FrequencyLowEngAlphabet = "eariotnslcudpmhgbfywkvxjqz";

    private short shift = 0;   //Right shift
    private short step = 0;
    private String base_text = "";
    private String str_out_text = "";

    public CaesarGUI(String title) throws HeadlessException {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
//-----------------------------------------------------------------------GUI-----------------------------------------------------------------------
        setBounds(300,300,1200,500);               //Set size and position of mainPanel

        //Create content panel
        Container ControlHost = getContentPane();
        ControlHost.setLayout(new FlowLayout());

        Font font = new Font("Impact", Font.PLAIN, 16);
        Font font_shift = new Font("Impact", Font.PLAIN, 22);

        inputText = new JTextArea(20, 20);              //Create input area
        inputText.setLineWrap(true);                                //Wrap text to new line when filling
        inputText.setWrapStyleWord(true);                           //Correct wrapping
        inputText.setBackground(Color.DARK_GRAY);
        inputText.setForeground(Color.WHITE);
        inputText.setFont(font);

        outputText = new JTextArea(20, 20);
        outputText.setLineWrap(true);
        outputText.setWrapStyleWord(true);
        outputText.setBackground(Color.DARK_GRAY);
        outputText.setForeground(Color.WHITE);
        outputText.setFont(font);

        shiftValueText = new JTextArea(1, 4);
        shiftValueText.setBackground(Color.DARK_GRAY);
        shiftValueText.setForeground(Color.WHITE);
        shiftValueText.setFont(font_shift);

        //Create JScrollPane for JTextArea
        JScrollPane jscroll_1 = new JScrollPane(
                inputText,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JScrollPane jscroll_2 = new JScrollPane(
                outputText,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        ControlHost.add(jscroll_1);
        ControlHost.add(jscroll_2);
        ControlHost.add(shiftValueText);
//-----------------------------------------------------------------------GUI-----------------------------------------------------------------------
//----------------------------------------------------------Encryption/Decryption Buttons----------------------------------------------------------
        encryptionBtn.addActionListener(e -> {
            GetShift();

            if(!(inputText.getText().equals(""))) {
                base_text = inputText.getText();
                str_out_text = "";

                Enc_Dec(shift, shift, shift, shift);
                outputText.setText(str_out_text);
            }
            else inputText.setText("\"Пример текста\"");
        });
        //-------------------------------------------------------------------------------------------------------------
        decryptionBtn.addActionListener(e -> {
            GetShift();

            if(!(inputText.getText().equals(""))) {
                base_text = inputText.getText();
                str_out_text = "";

                Enc_Dec((short) (LowRusAlphabet.length()-shift), (short) (HighRusAlphabet.length()-shift), (short) (LowEngAlphabet.length()-shift), (short) (HighEngAlphabet.length()-shift));
                outputText.setText(str_out_text);
            }
            else inputText.setText("\"Пример текста\"");
        });
        //-------------------------------------------------------------------------------------------------------------
        enumerationMethodBtn.addActionListener(e -> {
            if(!(inputText.getText().equals(""))) {
                base_text = inputText.getText();
                str_out_text = "";

                if(step >= 33) step = 0;
                str_out_text = str_out_text + (step+1) + "- ";
                Enc_Dec((short) (shift + step), (short) (shift + step), (short) (shift + step), (short) (shift + step));
                str_out_text+=";\n\n";
                step++;

                outputText.setText(str_out_text);
            }
            else inputText.setText("\"Пример текста\"");
        });
        //-------------------------------------------------------------------------------------------------------------
        frequencyBtn.addActionListener(e -> {
            if(!(inputText.getText().equals(""))) {
                base_text = inputText.getText();
                str_out_text = "";
                Frequency();
                outputText.setText(str_out_text);
            }
            else inputText.setText("\"Пример текста\"");
        });
        //-------------------------------------------------------------------------------------------------------------
        saveBtn.addActionListener(e -> {
            str_out_text = outputText.getText();
            File myFile = new File("CaesarText.txt");                        //Создание экземпляр (для большей безопасности)
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter(myFile));
                writer.write(str_out_text);
                writer.flush();                                                      //Освобождение буффера до его заполнения
                writer.close();                                                      //Закрытие файлового дескриптора для освобождения ресурсов
            }catch (IOException ex){
                ex.printStackTrace();
            }
        });
        //-------------------------------------------------------------------------------------------------------------
        uploadBtn.addActionListener(e -> {
            base_text = "";
            File myFile = new File("CaesarText.txt");
            try{
                BufferedReader reader = new BufferedReader(new FileReader(myFile));
                int c;
                while((c=reader.read())!=-1){                                       //Посимвольное считывание (с проверкой на окончание символов)
                    base_text+=(char)c;
                }
                reader.close();
                inputText.setText(base_text);
            }catch (IOException ex){
                ex.printStackTrace();
            }
        });
//----------------------------------------------------------Encryption/Decryption Buttons----------------------------------------------------------
    }

    public static void main(String[] args){
        JFrame frame = new CaesarGUI("Caesar's cipher");
        frame.setVisible(true);
    }

    private void Enc_Dec(short n_1, short n_2, short n_3, short n_4){
        for(int i = 0; i != base_text.length(); i++) {
            if(LowRusAlphabet.indexOf(base_text.charAt(i)) != -1) {
                str_out_text+=LowRusAlphabet.charAt((LowRusAlphabet.indexOf(base_text.charAt(i)) + n_1) % LowRusAlphabet.length());
            }
            else if(HighRusAlphabet.indexOf(base_text.charAt(i)) != -1){
                str_out_text+=HighRusAlphabet.charAt((HighRusAlphabet.indexOf(base_text.charAt(i)) + n_2) % HighRusAlphabet.length());
            }
            else if(LowEngAlphabet.indexOf(base_text.charAt(i)) != -1){
                str_out_text+=LowEngAlphabet.charAt((LowEngAlphabet.indexOf(base_text.charAt(i)) + n_3) % LowEngAlphabet.length());
            }
            else if(HighEngAlphabet.indexOf(base_text.charAt(i)) != -1){
                str_out_text+=HighEngAlphabet.charAt((HighEngAlphabet.indexOf(base_text.charAt(i)) + n_4) % HighEngAlphabet.length());
            }
            else str_out_text+=base_text.charAt(i);
        }
    }

    private void GetShift(){
        try{
            shift = Short.parseShort(shiftValueText.getText());
            shift = (short) Math.abs(shift);
        }catch (NumberFormatException ex){
            shift = 1;
            ex.printStackTrace();
        }
    }

    private void Frequency(){
        boolean dec_mode = false;
        int[] FreqHighRus = new int[33];
        int[] FreqLowRus = new int[33];
        int[] FreqHighEng = new int[26];
        int[] FreqLowEng = new int[26];

        while(true) {
            for (int i = 0; i != base_text.length(); i++) {
                if (LowRusAlphabet.indexOf(base_text.charAt(i)) != -1) {
                    if (!dec_mode)
                        FreqLowRus[LowRusAlphabet.indexOf(base_text.charAt(i))]++;
                    else
                        str_out_text+=FrequencyLowRusAlphabet.charAt(Math.abs(FreqLowRus[LowRusAlphabet.indexOf(base_text.charAt(i))]));

                } else if (HighRusAlphabet.indexOf(base_text.charAt(i)) != -1) {
                    if (!dec_mode)
                        FreqHighRus[HighRusAlphabet.indexOf(base_text.charAt(i))]++;
                    else
                        str_out_text+=FrequencyHighRusAlphabet.charAt(Math.abs(FreqHighRus[HighRusAlphabet.indexOf(base_text.charAt(i))]));

                } else if (LowEngAlphabet.indexOf(base_text.charAt(i)) != -1) {
                    if (!dec_mode)
                        FreqLowEng[LowEngAlphabet.indexOf(base_text.charAt(i))]++;
                    else
                        str_out_text+=FrequencyLowEngAlphabet.charAt(Math.abs(FreqLowEng[LowEngAlphabet.indexOf(base_text.charAt(i))]));

                } else if (HighEngAlphabet.indexOf(base_text.charAt(i)) != -1) {
                    if (!dec_mode)
                        FreqHighEng[HighEngAlphabet.indexOf(base_text.charAt(i))]++;
                    else
                        str_out_text+=FrequencyHighEngAlphabet.charAt(Math.abs(FreqHighEng[HighEngAlphabet.indexOf(base_text.charAt(i))]));
                }
                else if (dec_mode) str_out_text+=base_text.charAt(i);
            }

            if (!dec_mode) {
                for (short i = 0; i < 33; i++) {
                    if(FreqHighRus[MaxIndex(FreqHighRus)]!=0)
                        FreqHighRus[MaxIndex(FreqHighRus)] = -i;
                    if(FreqLowRus[MaxIndex(FreqLowRus)]!=0)
                        FreqLowRus[MaxIndex(FreqLowRus)] = -i;
                }
                for (short i = 0; i < 26; i++) {
                    if(FreqHighEng[MaxIndex(FreqHighEng)]!=0)
                        FreqHighEng[MaxIndex(FreqHighEng)] = -i;
                    if(FreqLowEng[MaxIndex(FreqLowEng)]!=0)
                        FreqLowEng[MaxIndex(FreqLowEng)] = -i;
                }
            } else break;
            dec_mode = true;
        }
    }

    private short MaxIndex(int[]arr){
        int max = 0;
        short max_index = 0;
        for(short i = 0; i < arr.length; i++){
            if(arr[i] >= max) {
                max = arr[i];
                max_index = i;
            }
        }
        return max_index;
    }
}
