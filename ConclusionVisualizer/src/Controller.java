import java.awt.event.ActionEvent;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import javax.swing.SwingUtilities;

public class Controller implements Runnable
{
	private CalculationEngine calculationEngine;
	private Vision widok;
	private BlockingQueue<ActionEvent> eventQueue;
	
	public Controller(CalculationEngine cE, Vision v, BlockingQueue<ActionEvent> q)
	{
		calculationEngine = cE;
		widok = v;
		eventQueue = q;
		wydajPolecenie("Start");
	}
	
	private void wydajPolecenie(String zdarzenie) {
		//POLECENIA POCHODZACE Z EKRANU SKLADANIA ZLECENIA
		if(zdarzenie == "Przycisk: Wprowadz zlecenie") {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					calculationEngine.odbierzDaneZlecenia(widok.podajDaneZlecenia());
					wydajPolecenie("Przycisk: Symuluj kolejna zmiane");
					wydajPolecenie("Przycisk: Wroc do symulacji");
				}
			});
		}
		//POLECENIA POCHODZACE Z EKRANU SYMULACJI
		else if(zdarzenie == "Przycisk: Zmien zlecenie") {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					widok.zmienEkran("ekranSkladaniaZlecenia");
				}
			});
		}
		else if(zdarzenie == "Przycisk: Szukaj dopasowan") {
			final String werdykt;		//stwierdzenie co znaleziono
			final boolean koniec;		//mowi czy pokazac przycisk konca
			final Object[] daneMojego = calculationEngine.podajDaneOstatniegoZlecenia();
			final Object[] daneDopasowania = calculationEngine.podajDopasowanie();
			werdykt = wydajWerdykt(daneMojego, daneDopasowania);
			//czy wyswietlac przycisk koncowy
			if(werdykt == "ZNALEZIONO SILNE DOPASOWANIE") koniec = true;
			else koniec = false;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					widok.odbierzDopasowanie(werdykt, koniec, daneMojego, daneDopasowania);
					widok.zmienEkran("ekranDopasowania");
				}
			});
		}
		else if(zdarzenie == "Przycisk: Symuluj kolejna zmiane") {
			symulujZmiany("oferta kupna");
			symulujZmiany("oferta sprzedazy");
			symulujDodawanie();
			wydajPolecenie("Przycisk: Wroc do symulacji");
		}
		//POLECENIA POCHODZACE Z EKRANU DOPASOWANIA
		else if(zdarzenie == "Przycisk: Wroc do symulacji" || zdarzenie == "Start") {
			final Object[][] tabK = calculationEngine.podajListeKupna(); 
			final Object[][] tabS = calculationEngine.podajListeSprzedazy();
			final Object[] daneZ;
			if(zdarzenie != "Start")
				daneZ = calculationEngine.podajDaneOstatniegoZlecenia();
			else
				daneZ = null;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					widok.odbierzDaneZlecenia(daneZ);
					widok.odbierzDaneTabel(tabK, tabS);
					widok.zmienEkran("ekranSymulacji");	
				}
			});
		}
		else if(zdarzenie == "Przycisk: Przejdz do realizacji") {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					widok.zmienEkran("ekranKoncowy");
				}
			});
		}
		//POLECENIA POCHODZACE Z EKRANU KONCOWEGO
		else if(zdarzenie == "Przycisk: Wyjdz z symulatora") {
			System.exit(0);		//zakoncz program
		}
		//PRZYPADEK ZGLOSZONEGO WYJATKU ODCZYTU Z KOLEJKI
		else if(zdarzenie == "Blad pobrania")
			return;
	}  //wydajPolecenie()
	
	//funkcja okresla rodzaj znalezionego dopasowania
	private String wydajWerdykt(Object[] daneMojego, Object[] daneDopasowania) {
		String werdykt;
		if((String)daneDopasowania[0] == "") {	//nie ma dopasowania
			werdykt = "NIE ZNALEZIONO ZADNEGO DOPASOWANIA";
		} //if
		//jesli szukalem ofert kupna
		else if(/*typ*/(String)daneDopasowania[4] == "oferta kupna") {
			if(/*cenaJednej*/(Integer)daneMojego[3] <= /*cenaJednej*/(Integer)daneDopasowania[3])
				werdykt = "ZNALEZIONO SILNE DOPASOWANIE";
			else
				werdykt = "ZNALEZIONO SLABE DOPASOWANIE";
		} //else if
		//jesli szukalem ofert sprzedazy
		else  {
			if(/*cenaJednej*/(Integer)daneMojego[3] >= /*cenaJednej*/(Integer)daneDopasowania[3])
				werdykt = "ZNALEZIONO SILNE DOPASOWANIE";
			else
				werdykt = "ZNALEZIONO SLABE DOPASOWANIE";
		} //else
		return werdykt;
	} //wydajWerdykt()
	
	//funkcja symuluje zachowania innych zleceniodawcow
	private void symulujZmiany(String typ) {
		int size = calculationEngine.podajRozmiarListy(typ);		//rozmiar przegladanej listy
		String werdykt;
		Object[] oferta, dopasowanie;
		Object[] ostatnie = calculationEngine.podajDaneOstatniegoZlecenia();
		
		//symulacja zmian istniejacych ofert
		for(int i = 0 ; i < size ; i++) {
			oferta = calculationEngine.podajDaneOferty(i, typ);
			//jezeli sprawdzam ostatnio dodana oferte
			if(saRowne(oferta, ostatnie))
				continue;
			dopasowanie = calculationEngine.podajDopasowanie(i, typ);
			werdykt = wydajWerdykt(oferta, dopasowanie);
			polecenieDoModelu(werdykt, i, typ);
			size = calculationEngine.podajRozmiarListy(typ);	//zabezpieczenie
		} //for
	} //symulujZmiany()
	
	//funkcja dodaje losowe oferty do list
	private void symulujDodawanie() {
		Random generator = new Random(seedGen.nextLong());			//generator ilosci nowych ofert
		int ileKupna = generator.nextInt(99999) % 3;						//ilosc ofert kupna
		int ileSprzedazy = generator.nextInt(99999) % 3;					//ilosc ofert sprzedazy
		//dodaje oferty kupna
		for(int i = 0 ; i < ileKupna ; i++)
			calculationEngine.dodajOferte(generator.nextInt(99999), generator.nextInt(99999),
							  generator.nextInt(99999), "oferta kupna");
		//dodaje oferty sprzedazy
		for(int i = 0 ; i < ileSprzedazy ; i++)
			calculationEngine.dodajOferte(generator.nextInt(99999), generator.nextInt(99999),
							  generator.nextInt(99999), "oferta sprzedazy");
	}  //symulujDodawanie()
	
	//sprawdza czy dwie oferty sa rowne
	private boolean saRowne(Object[] jeden, Object[] drugi) {
		if(jeden.equals(drugi))
			return true;
		else return false;
	} //saRowne()
	
	//zmienia lub usuwa oferty w modelu w zaleznosci od werdyktu dopasowania
	private void polecenieDoModelu(String werdykt, int indeks, String typ) {
		if(werdykt == "NIE ZNALEZIONO ZADNEGO DOPASOWANIA")
			;	//nie rob nic
		else if(werdykt == "ZNALEZIONO SLABE DOPASOWANIE")
			calculationEngine.zmienCene(indeks, typ);
		else if(werdykt == "ZNALEZIONO SILNE DOPASOWANIE") {
			calculationEngine.rozliczOferte(indeks, typ);
		}
	} //polecenieDoModelu()

	//////////////////////// METODA RUN //////////////////////////////////
	public void run() {
		ActionEvent zdarzenie;
		while(true) {
			try {
				zdarzenie = kolejka.take();
			}
			catch (InterruptedException ex) {
				zdarzenie = new ActionEvent(ex, 0, "Blad pobrania");
			}
			wydajPolecenie(zdarzenie.getActionCommand());
		}  //while
	} //run()
} //class Kontroler

/* TODO
 * -ew. symulacja zalezna od czesu a nie przycisku
 * -wydanie polecenia gdy Blad pobrania
 */ 



//-zapisywanie transakcji
//-JDBC
//-wybieranie spolki z listy, profile spolki, w bazie