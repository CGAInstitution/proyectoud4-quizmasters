package madstodolist.service.exception;

public class NotEnoughQuestionsException extends Exception{
    private String message;
    public NotEnoughQuestionsException(String message){
        super(message);
    }
}
