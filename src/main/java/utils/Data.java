package utils;


//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO

public class Data
{
    //public static final String adminPass = "you really thought I would share my password with you?";
    public static final String adminPass = "parola";
    //public static final String adminEmail = "mail@gmail.com";
    public static final String adminEmail = "mail@example.ro";
}



/*
    public void handleBackToMM() throws IOException
    {
        Stage stage = (Stage) this.root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource(Paths.mainMenu));
        Parent root = loader.load();
        MainMenuController mainMenuController = loader.getController();
        stage.setScene(new Scene(root));
        if (somethingChanged && patch.getPatchNotes() != null)
        {
            Utils.sendEmail(this.service.getUsers(), patch.getPatchNotes());
            mainMenuController.updateUsers(this.service.getUsers());
        }
        root.getScene().setFill(Color.BLACK);
    }*/


/*
    public String getPatchNotes()
    {
        if (this.patchList.isEmpty())
            return null;
        final String[] patchNotes =
                {
                        "The following changes were made to the items and/or characters from the game, in the new update: \n\n"
                };

        this.patchList.values().stream().sorted(Comparator.comparing(Pair::getValue)).forEach(
                x ->
                        patchNotes[0] = patchNotes[0] +
                                x.getValue().toString() +
                                "\n" +
                                x.getKey() + "\n\n");
        return patchNotes[0];
    }*/
