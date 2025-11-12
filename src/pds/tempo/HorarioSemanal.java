package pds.tempo;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Objects;

/**
 * Esta classe representa um horário de trabalho semanal, de segunda a domingo.
 * Cada horário diário é um HorarioDiario.
 */
public final class HorarioSemanal {

    private final HashMap<DayOfWeek, HorarioDiario> horarios = new HashMap<>();

    private static final HorarioSemanal SEMPRE_ABERTO = of(HorarioDiario.TODO_DIA,
            HorarioDiario.TODO_DIA, HorarioDiario.TODO_DIA, HorarioDiario.TODO_DIA,
            HorarioDiario.TODO_DIA, HorarioDiario.TODO_DIA, HorarioDiario.TODO_DIA);

    private static final HorarioSemanal SEMPRE_FECHADO = of(HorarioDiario.VAZIO, HorarioDiario.VAZIO,
            HorarioDiario.VAZIO, HorarioDiario.VAZIO,
            HorarioDiario.VAZIO, HorarioDiario.VAZIO, HorarioDiario.VAZIO);

    public static HorarioSemanal sempreFechado() {
        return SEMPRE_FECHADO;
    }

    public static HorarioSemanal sempreAberto() {
        return SEMPRE_ABERTO;
    }

    public static HorarioSemanal semanaUtil(HorarioDiario sg, HorarioDiario t, HorarioDiario qua, HorarioDiario qui,
            HorarioDiario sex) {
        return of(sg, t, qua, qui, sex, HorarioDiario.VAZIO, HorarioDiario.VAZIO);
    }

    public static HorarioSemanal semanaUtil(HorarioDiario[] hs) {
        if (hs.length != 5)
            throw new IllegalArgumentException();
        return of(hs[0], hs[1], hs[2], hs[3], hs[4], HorarioDiario.VAZIO, HorarioDiario.VAZIO);
    }

    public static HorarioSemanal of(HorarioDiario sg, HorarioDiario t, HorarioDiario qua, HorarioDiario qui,
            HorarioDiario sex, HorarioDiario sab, HorarioDiario dom) {
        HorarioDiario hds[] = { sg, t, qua, qui, sex, sab, dom };
        return of(hds);
    }

    public static HorarioSemanal of(HorarioDiario[] hs) {
        if (hs.length != 7)
            throw new IllegalArgumentException();
        HorarioSemanal h = new HorarioSemanal();
        for (int i = 0; i < hs.length; i++)
            h.definirHorario(DayOfWeek.values()[i], Objects.requireNonNull(hs[i]));
        return h;
    }

    public void definirHorario(DayOfWeek day, HorarioDiario h) {
        horarios.put(day, Objects.requireNonNull(h));
    }

    public HorarioDiario getHorarioDia(DayOfWeek d) {
        return horarios.get(d);
    }

    public boolean estaDentroHorario(LocalDateTime time) {
        return getHorarioDia(time.getDayOfWeek()).contem(LocalTime.from(time));
    }

    public boolean estaDentroHorario(DayOfWeek dia, LocalTime hora) {
        return getHorarioDia(dia).contem(LocalTime.from(hora));
    }
}
