
import javax.swing.JOptionPane;



class QueDialogs {

    public void showDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Default Message", JOptionPane.PLAIN_MESSAGE);
    }
}

class infoDialog extends QueDialogs {
    @Override
    public void showDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}

class warningDialog extends QueDialogs {
    @Override
    public void showDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}

class askDialog {
    public String showDialog(String message, String title) {
        String userInput = JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
        return userInput;
    }
}
class optionDialog {
    public int showDialog(
            String[] opt,
            String message,
            String title
    ) {
        int userInput = JOptionPane.showOptionDialog(
                null,
                message,
                title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opt,
                opt[0]);
        
        return userInput;
    }
}    

