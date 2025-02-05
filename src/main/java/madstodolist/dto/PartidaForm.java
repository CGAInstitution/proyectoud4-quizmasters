package madstodolist.dto;

import java.time.LocalDateTime;

public class PartidaForm {
    private String modoDeJuego;
    private LocalDateTime dateTime;
    private boolean joinable;

    public PartidaForm(String modoDeJuego, LocalDateTime dateTime, boolean joinable) {
        this.modoDeJuego = modoDeJuego;
        this.dateTime = dateTime;
        this.joinable = joinable;
    }

    public PartidaForm() {
    }

    public String getModoDeJuego() {
        return modoDeJuego;
    }

    public void setModoDeJuego(String modoDeJuego) {
        this.modoDeJuego = modoDeJuego;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isJoinable() {
        return joinable;
    }

    public void setJoinable(boolean joinable) {
        this.joinable = joinable;
    }
}
