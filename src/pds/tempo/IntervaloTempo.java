package pds.tempo;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa um intervalo de tempo entre dois LocalDateTime de início
 * (inclusive) e fim (inclusive).
 * Esta classe é imutável.
 */
public final class IntervaloTempo {
    private final LocalDateTime inicio;
    private final LocalDateTime fim;

    /**
     * Cria um IntervaloTempo com um dado inicio e fim, ambos inclusive. O início
     * tem de ser antes ou igual a fim.
     * 
     * @param inicio o início do intervalo de tempo (inclusive)
     * @param fim    o fim do intervalo de tempo (inclusive)
     */
    public IntervaloTempo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio.isAfter(fim))
            throw new IllegalArgumentException("inicio não pode ser depois de fim");
        this.inicio = inicio;
        this.fim = fim;
    }

    /**
     * Cria um intervalo de tempo com um dado início e respetiva duração em segundos
     * 
     * @param inicio  o início do intervalo de tempo (inclusive)
     * @param duracao duração em segundos do intervalo
     */
    public IntervaloTempo(LocalDateTime inicio, long duracao) {
        if (duracao < 0)
            throw new IllegalArgumentException("duracao não pode ser negativa");
        this.inicio = Objects.requireNonNull(inicio);
        this.fim = inicio.plusSeconds(duracao);
    }

    /**
     * Retorna um intervalo de tempo com um dado inicio e fim, ambos inclusive. O
     * início tem de ser antes ou igual a fim.
     * 
     * @param inicio o início do intervalo de tempo (inclusive)
     * @param fim    o fim do intervalo de tempo (inclusive)
     * @return o intervalo de tempo especificado
     */
    public static IntervaloTempo entre(LocalDateTime inicio, LocalDateTime fim) {
        return new IntervaloTempo(inicio, fim);
    }

    /**
     * Retorna um intervalo de tempo com um dado início e respetiva duração em
     * segundos
     * 
     * @param inicio  o início do intervalo de tempo (inclusive)
     * @param duracao duração em segundos do intervalo
     * @return o intervalo de tempo especificado
     */
    public static IntervaloTempo entre(LocalDateTime inicio, long duracao) {
        return new IntervaloTempo(inicio, duracao);
    }

    /**
     * Retorna um intervalo de tempo desde agora até ao tempo final indicado
     * (inclusive)
     * 
     * @param fim o início do intervalo de tempo (inclusive)
     * @return o intervalo de tempo especificado
     */
    public static IntervaloTempo ate(LocalDateTime fim) {
        return new IntervaloTempo(LocalDateTime.now(), fim);
    }

    /**
     * Retorna um intervalo de tempo desde um tempo inicial (inclusive) até agora
     * 
     * @param inicio o início do intervalo de tempo (inclusive)
     * @return o intervalo de tempo especificado
     */
    public static IntervaloTempo desde(LocalDateTime inicio) {
        return new IntervaloTempo(inicio, LocalDateTime.now());
    }

    /**
     * retorna o fim do intervalo
     * 
     * @return o fim do intervalo
     */
    public LocalDateTime getFim() {
        return fim;
    }

    /**
     * retorna o início do intervalo
     * 
     * @return o início do intervalo
     */
    public LocalDateTime getInicio() {
        return inicio;
    }

    /**
     * Retorna a duração do intervalo.
     * 
     * @return a duração do intervalo
     */
    public Duration duracao() {
        return Duration.between(inicio, fim);
    }

    /**
     * Indica se uma dada LocalDateTime está dentro deste intervalo
     * 
     * @param data a data a verificar se está dentro do intervalo
     * @return true, se está dentro do intervalo
     */
    public boolean contem(LocalDateTime data) {
        return inicio.compareTo(data) <= 0 && fim.compareTo(data) >= 0;
    }

    /**
     * Indica se este intervalo interseta outro.
     * 
     * @param outro o intervalo que se pretende verirficar se se interseta, não pode
     *              ser null
     * @return true se se intersetarem
     */
    public boolean interseta(IntervaloTempo outro) {
        return !inicio.isAfter(outro.fim) && !fim.isBefore(outro.inicio);
    }

    /**
     * Indica se este intervalo engloba outro. Englobar significa que o outro
     * intervalo está completamente dentro deste.
     * 
     * @param outro o intervalo a testar
     * @return true se o outro está englobado neste
     */
    public boolean engloba(IntervaloTempo outro) {
        return !inicio.isAfter(outro.inicio) && !fim.isBefore(outro.fim);
    }

    /**
     * Retorna um intervalo de tempo que é a interseção deste com outro intervalo.
     * Os intervalos têm de se intersetar, senão é atirada a exceção
     * DateTimeException
     * 
     * @param outro o intervalo com que se vai intersetar
     * @return A interseção entre os dois intervalos
     * @throws DateTimeException se os intervalos não se intersetarem
     */
    public IntervaloTempo intersecao(IntervaloTempo outro) {
        if (!interseta(outro))
            throw new DateTimeException("intervalos não se intersetam");
        return new IntervaloTempo(inicio.isAfter(inicio) ? inicio : outro.inicio,
                fim.isBefore(outro.fim) ? fim : outro.fim);
    }

    /**
     * Retorna um intervalo de tempo que é a união deste com outro intervalo.
     * Os intervalos têm de se intersetar, senão é atirada a exceção
     * DateTimeException
     * 
     * @param outro o intervalo com que se vai unir
     * @return A união entre os dois intervalos
     * @throws DateTimeException se os intervalos não se intersetarem
     */
    public IntervaloTempo uniao(IntervaloTempo outro) {
        if (!interseta(outro))
            throw new DateTimeException("intervalos não se intersetam");
        return new IntervaloTempo(inicio.isBefore(inicio) ? inicio : outro.inicio,
                fim.isAfter(outro.fim) ? fim : outro.fim);
    }

    /**
     * Retorna um novo intervalo de tempo com o mesmo inicio deste mas com o fim
     * indicado
     * 
     * @param novoFim o fim do novo intervalo
     * @return um intervalo com o mesmo inicio deste e com o fim especificado
     */
    public IntervaloTempo comFimA(LocalDateTime novoFim) {
        return new IntervaloTempo(inicio, novoFim);
    }

    /**
     * Retorna um novo intervalo de tempo com o mesmo fim deste mas com o início
     * indicado
     * 
     * @param novoInicio o início do novo intervalo
     * @return um intervalo com o início indicado e com o mesmo fim deste
     */
    public IntervaloTempo comInicioA(LocalDateTime novoInicio) {
        return new IntervaloTempo(novoInicio, fim);
    }
}
