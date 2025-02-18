package quizmasters.dto;

import java.time.LocalDateTime;

public class PartidaForm {
    private Long modoDeJuegoId;
    private LocalDateTime dateTime;
    private boolean joinable;

    public PartidaForm(Long modoDeJuegoId, LocalDateTime dateTime, boolean joinable) {
        this.modoDeJuegoId = modoDeJuegoId;
        this.dateTime = dateTime;
        this.joinable = joinable;
    }

    public PartidaForm() {
    }

    public Long getModoDeJuegoId() {
        return modoDeJuegoId;
    }

    public void setModoDeJuegoId(Long modoDeJuegoId) {
        this.modoDeJuegoId = modoDeJuegoId;
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
