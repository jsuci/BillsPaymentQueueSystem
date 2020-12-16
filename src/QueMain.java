
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QueMain {
    QueGUI gui;
    QueDB db;
    
    int totalEntries;
    int min_window;
    int max_window;
    int track;
    
    askDialog askMsg;
    infoDialog infoMsg;
    warningDialog warningMsg;
    optionDialog opt;
    
    String[][] entries;
    String currCustNum;
    String currCustName;
    String currWindowNum;
    
    public QueMain(QueGUI parentGUI) {
        db = new QueDB("root", "", "que_db");
        db.connectDB();
        
        gui = parentGUI;
        min_window = 1;
        max_window = 5;
        track = -1;
        totalEntries = db.countEntries();
        entries = db.getEntries();
        
        askMsg = new askDialog();
        infoMsg = new infoDialog();
        warningMsg = new warningDialog();
        opt = new optionDialog();
        
        currCustNum = "00";
        currCustName = "---";
        currWindowNum = "00";
    }

    public boolean inValidName(String name) {
        Pattern pattern = Pattern.compile("[^a-zA-Z ]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        boolean matchFound = matcher.find();
        
        return matchFound || name == null || name.isBlank();
        
    }
    
    public void addCustomer() {
        String userInput = askMsg.showDialog("Enter your name:", "Add Customer");
        
        if (min_window == max_window) {
            min_window = 1;
        }
        
        if (inValidName(userInput)) {
            warningMsg.showDialog("Enter a valid name please.");        
        } else {
            boolean isAdded = db.insertDATA(userInput, min_window);

            if (isAdded) {

               String[] lastCustomer = db.getLastEntry();
               String message = (
                       "Thank you " + lastCustomer[1] + "!\n" +
                       "Your customer # is " + lastCustomer[0] + ".\n" +
                       "Pay your bills @ Window " + lastCustomer[2]
               );

               entries = db.getEntries();
               totalEntries = db.countEntries();
               infoMsg.showDialog(message);
               min_window++;
               
            } else {
                String message = userInput + " already on the list.";
                warningMsg.showDialog(message); 
            }
        }
    
    }
    
    public void callCustomer() {
        if (track == totalEntries - 1) {
            warningMsg.showDialog("You have reached the end of the list.");
        } else {
            track++;
            
            
            currCustNum = entries[track][0];
            currCustName = entries[track][1];
            currWindowNum = entries[track][2];

            System.out.println(currCustName);

        }
    }
    
    public void recallCustomer() {
        if (track == 0 || track == -1) {
            warningMsg.showDialog("You have reached the beginning of the list.");
        } else {
            track--;
            
            entries = db.getEntries();
            currCustNum = entries[track][0];
            currCustName = entries[track][1];
            currWindowNum = entries[track][2];
            
            System.out.println(currCustName);

        }
    }
    
    public void resetCustomer() {
        String[] choice = {"OK", "Cancel"};
        
        int output = opt.showDialog(
                choice,
                "Are you sure you want to delete all entries?",
                "Reset Entries"
        );
        
        if (output == 0) {
            db.deleteEntries();
            track = -1;
            currCustNum = "00";
            currCustName = "---";
            currWindowNum = "00";
            
            entries = null;
            totalEntries = 0;
        }
        
    }
    
    public void importCustomer(File file) {
        try {
            InputStream fileIn = new FileInputStream(file);
            InputStream reader = new BufferedInputStream(fileIn);
            ObjectInput objIn = new ObjectInputStream(reader);
            
            try {
                String[][] importedEntries = (String[][]) objIn.readObject();
                
                for (String[] importedEntrie : importedEntries) {
                    String impCustName = importedEntrie[1];
                    int impCustWindow = Integer.valueOf(importedEntrie[2]);
                    boolean isAdded = db.insertDATA(impCustName, impCustWindow);
                    if (!isAdded) {
                        System.out.println(impCustName + " already added.");
                    }
                }
                
                infoMsg.showDialog("Done importing customer data.");
                totalEntries = db.countEntries();
                entries = db.getEntries();
                
            } catch (ClassNotFoundException e) {
                System.out.println(e);
            }
            
        } catch (IOException e) {
            System.out.println(e);
        }
    } 
    
    public void exportCustomer(File file) {
            try {
                String[][] customeRentries = db.getEntries();
                FileOutputStream fileOut = new FileOutputStream(file);
                ObjectOutputStream writer = new ObjectOutputStream(fileOut);
                writer.writeObject(customeRentries);
                infoMsg.showDialog("Done exporting customer data.");
            } catch (IOException e) {
                System.out.println(e);
                warningMsg.showDialog("An error occured.");
            }
    }
}