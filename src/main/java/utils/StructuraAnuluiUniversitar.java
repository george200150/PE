package utils;

public class StructuraAnuluiUniversitar {
    private String id;//1
    private String anulUniversitar;//2019-2020
    private final StructuraSemestru sem1;
    private final StructuraSemestru sem2;

    public StructuraAnuluiUniversitar(String id, String anulUniversitar, StructuraSemestru sem1, StructuraSemestru sem2) {
        this.id = id;
        this.anulUniversitar = anulUniversitar;
        this.sem1 = sem1;
        this.sem2 = sem2;
    }

    public String getId() {
        return id;
    }

    public String getAnulUniversitar() {
        return anulUniversitar;
    }

    public StructuraSemestru getSem1() {
        return sem1;
    }

    public StructuraSemestru getSem2() {
        return sem2;
    }


}


/*
public class StructuraSemestru {
    int anUniversitar;
    int semestru;
    private LocalDate startSemester;
    private LocalDate beginHolyday;
    private LocalDate endHolyday;
    private LocalDate endSemester;

}
    Exemplu pt sem 1, anUniv 2019-2020: datele le-am putea stoca in fisierul sem1.txt
        startSemester="30.09.2019"
        beginHolyday="23.12.2019"
        endHolyday="05.01.2020"
        endSemester="17.01.2020"

Avand o data curenta, am putea determina daca aceasta este in semestrul definit mai sus si, daca da, putem  determina numarul saptamanii din semestru pentru acea data.
PT TESTAREA FUNCTIEI CARE NE RETURNEAZA SAPTAMANA CURENTA DIN SEMESTRU:
Deci, la testarea functiei care va returneaza saptamana curenta din cadrul unui semestru, trebuie sa aveti in vedere diferite valori pt data curenta,
 chiar si valori care nu apartin semestrului (eu pot rula aplicatia la vara, sau  in sem 2 pt o disciplina care se preda in sem 1).
*/
