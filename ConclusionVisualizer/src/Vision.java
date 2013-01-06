import java.util.concurrent.BlockingQueue;
import java.awt.*;
import java.awt.event.*;	//nie wiem czemu nie wystarczy awt.*
import javax.swing.*;

public class Vision
{
	private final int szerokosc = 800, wysokosc = 500;						//rozmiary glownej ramki
	private final BlockingQueue<ActionEvent> kolejka;						//referencja do kolejki blokujacej
	private Object[] daneZlecenia = new Object[5];							//dane ostatnio zlozonego zlecenia
	//Obiekty do wyswietlenia
	private JFrame glownaRamka = new JFrame();												//glowna ramka
	private EkranSymulacji ekranSymulacji = new EkranSymulacji(wysokosc);
	private EkranSkladaniaZlecenia ekranSkladaniaZlecenia = new EkranSkladaniaZlecenia(wysokosc);
	private EkranDopasowania ekranDopasowania = new EkranDopasowania(wysokosc);
	private EkranKoncowy ekranKoncowy = new EkranKoncowy(wysokosc);
	
	public Vision(BlockingQueue<ActionEvent> k)
	{
		kolejka = k;
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				glownaRamka.setTitle("Symulator GPW");
				glownaRamka.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				glownaRamka.setSize(szerokosc, wysokosc);
			}
		});
	}
	
	//////////////////////////// INTERFACE ///////////////////////////////////
	
	public void zmienEkran(String ekran) {
		if(ekran == "ekranSymulacji")
			glownaRamka.setContentPane(ekranSymulacji);
		else if(ekran == "ekranSkladaniaZlecenia")
			glownaRamka.setContentPane(ekranSkladaniaZlecenia);
		else if(ekran == "ekranDopasowania")
			glownaRamka.setContentPane(ekranDopasowania);
		else if(ekran == "ekranKoncowy")
			glownaRamka.setContentPane(ekranKoncowy);
		glownaRamka.setVisible(true);
	} //zmienEkran()
	
	public void odbierzDaneTabel(Object[][] daneTabeliKupna, Object[][] daneTabeliSprzedazy) {
		ekranSymulacji.zmienTabele(false, daneTabeliKupna);
		ekranSymulacji.zmienTabele(true, daneTabeliSprzedazy);
	}
	
	public Object[] podajDaneZlecenia() {
		return daneZlecenia;
	}
	
	public void odbierzDaneZlecenia(Object[] dane) {
		ekranSymulacji.zmienZlecenie(dane);
	}  //zmienEkran()
	
	public void odbierzDopasowanie(String werdykt, boolean koniec, Object[] daneMojego, Object[] daneDop) {
		ekranDopasowania.zmienWerdykt(werdykt);
		ekranDopasowania.pokazPrzyciskKoniec(koniec);
		ekranDopasowania.zmienZlecenia(daneMojego, daneDop);	
	}  //zglosDopasowanie()

	///////////////////////////// KLASY EKRANOW ////////////////////////////////////////
	
	private class EkranSymulacji extends JPanel implements ActionListener {
		/**
		 * Ekran pokazujacy przebieg symulacji gieldy. Zawiera dane dotyczace zlozonego zlecenia
		 * oraz tabele aktualnych zlecen kupna i sprzedazy. Jest sluchaczem trzech guzikow, ktore
		 * zawiera. Komende wydarzenia tych guzikow przekazuje do kontrolera
		 */
		//pomocnicze zmienne
		private final String[] nazwyKolumn = {"Nazwa spolki", "Cena akcji [PLN]"};
		private JButton b;
		//glowne panele
		private JPanel panelZlecenia = new JPanel();		//panel z danymi zlozonego zlecenia
		private JPanel panelTabel = new JPanel();			//panel z tabelami
		private JPanel panelPrzyciskow = new JPanel();			//dolny panel z guzikami
		//komponenty powiazane z panelami
		private JPanel panelKupna = new JPanel();			//skladowe panelu z tabelami
		private JPanel panelSprzedazy = new JPanel();	
		private JTable tabelaKupna = new JTable();			//tabele aktualnych zlecen
		private JTable tabelaSprzedazy = new JTable();
		
		public EkranSymulacji(int wysokosc) {
		//ustawienie glownej ramki
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(panelZlecenia);
			this.add(panelTabel);
			this.add(panelPrzyciskow);
		//panel z danymi zlozonego zlecenia
			panelZlecenia.setBorder(BorderFactory.createTitledBorder("Dane zlozonego zlecenia"));
			panelZlecenia.setLayout(new GridLayout(0, 4));
			panelZlecenia.setMaximumSize(new Dimension(99999, 2*wysokosc/10));
		//panele z tabelami zlecen
			panelTabel.setLayout(new BoxLayout(panelTabel, BoxLayout.X_AXIS));
			panelTabel.setMaximumSize(new Dimension(99999, 99999));
			panelTabel.add(panelKupna);
			panelTabel.add(panelSprzedazy);
			panelKupna.setBorder(BorderFactory.createTitledBorder("Zlecenia kupna"));
			panelKupna.setLayout(new BoxLayout(panelKupna, BoxLayout.Y_AXIS));
			panelKupna.add(tabelaKupna);
			panelSprzedazy.setBorder(BorderFactory.createTitledBorder("Zlecenia sprzedazy"));
			panelSprzedazy.setLayout(new BoxLayout(panelSprzedazy, BoxLayout.Y_AXIS));
			panelSprzedazy.add(tabelaSprzedazy);
		//panel z guzikami
			panelPrzyciskow.setBorder(BorderFactory.createTitledBorder("Menu"));
			panelPrzyciskow.setMaximumSize(new Dimension(99999, wysokosc/10));
			b = new JButton("Szukaj dopasowan");
			panelPrzyciskow.add(b, BorderLayout.CENTER);
			b.setActionCommand("Przycisk: Szukaj dopasowan");
			b.addActionListener(this);
			b = new JButton("Symuluj kolejna zmiane");
			panelPrzyciskow.add(b, BorderLayout.EAST);
			b.setActionCommand("Przycisk: Symuluj kolejna zmiane");
			b.addActionListener(this);
			b = new JButton("Zloz zlecenie");
			panelPrzyciskow.add(b, BorderLayout.WEST);
			b.setActionCommand("Przycisk: Zmien zlecenie");
			b.addActionListener(this);
		} // EkranSymulacji()
		
		public void zmienTabele(boolean tabela, Object[][] daneWierszy) {
			//funkcja wywolywana przez Widok w celu aktualizacji danych w tabelach
			//tabela == false <=> tabelaKupna
			//tabela == true <=> tabelaSprzedazy
			if(!tabela)	{	//wybrano tabelaKupna
				panelKupna.removeAll();
				tabelaKupna = new JTable(daneWierszy, nazwyKolumn);
				panelKupna.add(tabelaKupna.getTableHeader());
				panelKupna.add(tabelaKupna);
				tabelaKupna.setFillsViewportHeight(true);
				tabelaKupna.setEnabled(false);
			}
			else {			//wybrano tabelaSprzedazy
				panelSprzedazy.removeAll();
				tabelaSprzedazy = new JTable(daneWierszy, nazwyKolumn);
				panelSprzedazy.add(tabelaSprzedazy.getTableHeader());
				panelSprzedazy.add(tabelaSprzedazy);
				tabelaSprzedazy.setFillsViewportHeight(true);
				tabelaSprzedazy.setEnabled(false);
			}
		}  //zmienTabele()
		
		public void zmienZlecenie(Object[] daneZlecenia) {
			if(daneZlecenia == null)
				return;
			panelZlecenia.removeAll();
			panelZlecenia.add(new JLabel("Zleceniodawca:"));
			panelZlecenia.add(new JLabel("Spolka:"));
			panelZlecenia.add(new JLabel("Ilosc akcji:"));
			panelZlecenia.add(new JLabel("Cena akcji:"));
			panelZlecenia.add(new JLabel(daneZlecenia[0].toString()));
			panelZlecenia.add(new JLabel(daneZlecenia[1].toString()));
			panelZlecenia.add(new JLabel(daneZlecenia[2].toString()));
			panelZlecenia.add(new JLabel(daneZlecenia[3].toString()));
			b.setLabel("Zmien zlecenie");
		} //zmienZlecenie()
		
		public void actionPerformed(ActionEvent e) {
			while(true) { 
				try { 
				 	kolejka.put(e);
				 	break;
				 }
				 catch (InterruptedException ex) {continue;}
			} //while()
		} //actionPerformed()
	} //class EkranSymulacji

	private class EkranSkladaniaZlecenia extends JPanel implements ActionListener {
		/**
		 * Ekran pozwalajacy na zlozenie zlecenia kupna lub sprzedazy. Program wyswietla
		 * ten ekran na poczatku.
		 */
		//pomocnicze zmienne
		private final String[] typy = {"oferta kupna", "oferta sprzedazy"};
		private final String[] spolki = {"Winterfell", "Budimox",
										"NowePrawo", "RusIT", "OstraSzabla" };
		private final SpinnerModel modelCeny = new SpinnerNumberModel(0, 0, 3000, 1);
		private final SpinnerModel modelIlosci = new SpinnerNumberModel(0, 0, 99999, 1);
		private JButton b;
		private JPanel p;
		//glowne panele
		private JPanel panelEdycji = new JPanel();			//panel z polami do wpisania danych
		private JPanel panelPrzyciskow = new JPanel();		//dolny panel z guzikami
		//komponenty powiazane z panelami
		private JTextField zleceniodawca = new JTextField(20);
		private JComboBox spolka = new JComboBox(spolki);
		private JComboBox typ = new JComboBox(typy);
		private JSpinner cena = new JSpinner(modelCeny);
		private JSpinner ilosc = new JSpinner(modelIlosci);
		
		public EkranSkladaniaZlecenia(int wysokosc) {
		//ustawienie glownej ramki
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(panelEdycji);
			this.add(panelPrzyciskow);
		//panel z danymi zlozonego zlecenia
			panelEdycji.setBorder(BorderFactory.createTitledBorder("Podaj dane zlecenia"));
			panelEdycji.setLayout(new GridLayout(2, 3));
			panelEdycji.setMaximumSize(new Dimension(99999, 99999));
			p = new JPanel(); p.add(new JLabel("Skladajacy:")); p.add(zleceniodawca);
			panelEdycji.add(p);
			p = new JPanel(); p.add(new JLabel("Cena akcji:")); p.add(cena);
			panelEdycji.add(p);
			p = new JPanel(); p.add(new JLabel("Typ zlecenia:")); p.add(typ);
			panelEdycji.add(p);
			p = new JPanel(); p.add(new JLabel("Nazwa spolki:")); p.add(spolka);
			panelEdycji.add(p);
			p = new JPanel(); p.add(new JLabel("Ilosc akcji:")); p.add(ilosc);
			panelEdycji.add(p);
		//panel z guzikami
			panelPrzyciskow.setBorder(BorderFactory.createTitledBorder("Menu"));
			panelPrzyciskow.setMaximumSize(new Dimension(99999, wysokosc/10));
			b = new JButton("Wprowadz zlecenie");
			panelPrzyciskow.add(b, BorderLayout.EAST);
			b.setActionCommand("Przycisk: Wprowadz zlecenie");
			b.addActionListener(this);
		} // EkranSymulacji()
		
		public void actionPerformed(ActionEvent e) {
			daneZlecenia[0] = zleceniodawca.getText();
			daneZlecenia[1] = spolka.getSelectedItem();
			daneZlecenia[2] = ilosc.getValue();
			daneZlecenia[3] = cena.getValue();
			daneZlecenia[4] = typ.getSelectedItem();
			while(true) { 
				try { 
				 	kolejka.put(e);
				 	break;
				 }
				 catch (InterruptedException ex) {continue;}
			} //while()	
		} //actionPerformed()
	}  //class EkranSkladaniaZlecenia
	
	private class EkranDopasowania extends JPanel implements ActionListener {
		/**
		 * Ekran wyswietla znalezione dopasowanie, pokazuje wiadomosc,
		 * ze po zmianach pasoawloby jakies inne zlecenie, lub oznajmia,
		 * ze nie kompletnie zadnych dopasowan
		 */
		//glowne panele
		private JPanel panelInformacji = new JPanel();		//panel z info o dopasowaniach
		private JPanel panelPrzyciskow = new JPanel();		//dolny panel z guzikami
		//komponenty powiazane z panelami
		private JLabel werdykt = new JLabel("");			//mowi czy znaleziono dopasowanie
		private JPanel panelMojego = new JPanel();			//panel z info zlozonego zlecenia
		private JPanel panelDopasowanego = new JPanel();	//panel z info dopasowanego zlecenia
		private JButton guzikWroc;							//pozwala odrzucic dopasownie, wrocic do symulacji
		private JButton guzikZmiana;						//wyswietla sie, gdy znaleziono slabe dopasowanie -
															// - pozwala zmienic zlozona oferte
		private JButton guzikKoniec;						//wyswietla sie, gdy znaleziono silne dopasowanie
		
		
		public EkranDopasowania (int wysokosc) {
		//ustawienie glownej ramki
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(panelInformacji);
			this.add(panelPrzyciskow);
		//panel z info o dopasowaniu
			panelInformacji.setBorder(BorderFactory.createTitledBorder("Informacje o dopasowaniu"));
			panelInformacji.setLayout(new GridLayout(3, 1));
			panelInformacji.setMaximumSize(new Dimension(99999, 99999));
			panelInformacji.add(werdykt);
			panelMojego.setBorder(BorderFactory.createTitledBorder("Dane zlozonego zlecenia"));
			panelMojego.setLayout(new GridLayout(0, 4));
			panelInformacji.add(panelMojego);
			panelDopasowanego.setBorder(BorderFactory.createTitledBorder("Dane dopasowanego zlecenia"));
			panelDopasowanego.setLayout(new GridLayout(0, 4));
			panelInformacji.add(panelDopasowanego);
		//panel z guzikami
			panelPrzyciskow.setBorder(BorderFactory.createTitledBorder("Menu"));
			panelPrzyciskow.setMaximumSize(new Dimension(99999, wysokosc/10));
			guzikWroc = new JButton("Wroc do symulacji");
			guzikWroc.setActionCommand("Przycisk: Wroc do symulacji");
			guzikWroc.addActionListener(this);
			panelPrzyciskow.add(guzikWroc, BorderLayout.WEST);
			guzikZmiana = new JButton("Zmien zlecenie");
			guzikZmiana.setActionCommand("Przycisk: Zmien zlecenie");
			guzikZmiana.addActionListener(this);
			panelPrzyciskow.add(guzikZmiana, BorderLayout.CENTER);
			guzikKoniec = new JButton("Przejdz do realizacji");
			guzikKoniec.setActionCommand("Przycisk: Przejdz do realizacji");
			guzikKoniec.addActionListener(this);
			panelPrzyciskow.add(guzikKoniec, BorderLayout.EAST);
		} // EkranDopasowania()
		
		public void zmienZlecenia(Object[] daneMojego, Object[] daneDopasowanego) {
			panelMojego.removeAll();
			panelMojego.add(new JLabel("Zleceniodawca:"));
			panelMojego.add(new JLabel("Spolka:"));
			panelMojego.add(new JLabel("Ilosc akcji:"));
			panelMojego.add(new JLabel("Cena akcji:"));
			panelMojego.add(new JLabel(daneMojego[0].toString()));
			panelMojego.add(new JLabel(daneMojego[1].toString()));
			panelMojego.add(new JLabel(daneMojego[2].toString()));
			panelMojego.add(new JLabel(daneMojego[3].toString()));
			
			panelDopasowanego.removeAll();
			panelDopasowanego.add(new JLabel("Zleceniodawca:"));
			panelDopasowanego.add(new JLabel("Spolka:"));
			panelDopasowanego.add(new JLabel("Ilosc akcji:"));
			panelDopasowanego.add(new JLabel("Cena akcji:"));
			panelDopasowanego.add(new JLabel(daneDopasowanego[0].toString()));
			panelDopasowanego.add(new JLabel(daneDopasowanego[1].toString()));
			panelDopasowanego.add(new JLabel(daneDopasowanego[2].toString()));
			panelDopasowanego.add(new JLabel(daneDopasowanego[3].toString()));
		} //zmienZlecenia()
		
		public void zmienWerdykt(String w) {
			panelInformacji.removeAll();
			werdykt = new JLabel(w);
			panelInformacji.add(werdykt);
			panelInformacji.add(panelMojego);
			panelInformacji.add(panelDopasowanego);
		}
		
		public void pokazPrzyciskKoniec(boolean bool) {
			guzikKoniec.setEnabled(bool);
		}
		
		public void actionPerformed(ActionEvent e) {
			while(true) { 
				try { 
				 	kolejka.put(e);
				 	break;
				 }
				 catch (InterruptedException ex) {continue;}
			} //while()	
		} //actionPerformed()
	}  //class EkranDopasowania
	
	private class EkranKoncowy extends JPanel implements ActionListener {
		/**
		 * Ekran wyswietlany po znalezieniu silnego dopasowania jako
		 * informacja o pomyslnie zakonczonej transakcji
		 */
		//glowne panele
		private JPanel panelInformacji = new JPanel();		//panel z info o dopasowaniach
		private JPanel panelPrzyciskow = new JPanel();		//dolny panel z guzikami
		//komponenty powiazane z panelami
		private JButton guzikKonca;							//przycisk konczenia symulacji
		
		public EkranKoncowy(int wysokosc) {
		//ustawienie glownej ramki
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.add(panelInformacji);
			this.add(panelPrzyciskow);
		//panel z info o dopasowaniu
			panelInformacji.setLayout(new BoxLayout(panelInformacji, BoxLayout.LINE_AXIS));
			panelInformacji.setMaximumSize(new Dimension(99999, 99999));
			panelInformacji.add(new JLabel("TRANSAKCJA ZAKONCZONA POMYSLNIE"));
		//panel z guzikami
			panelPrzyciskow.setBorder(BorderFactory.createTitledBorder("Menu"));
			panelPrzyciskow.setMaximumSize(new Dimension(99999, wysokosc/10));
			guzikKonca = new JButton("Wyjdz z symulatora");
			guzikKonca.setActionCommand("Przycisk: Wyjdz z symulatora");
			guzikKonca.addActionListener(this);
			panelPrzyciskow.add(guzikKonca, BorderLayout.CENTER);
		} // EkranDopasowania()
		
		public void actionPerformed(ActionEvent e) {
			while(true) { 
				try { 
				 	kolejka.put(e);
				 	break;
				 }
				 catch (InterruptedException ex) {continue;}
			} //while()	
		} //actionPerformed()
	}  //class EkranKoncowy

} //class Widok

/* TODO
 * -ew dopasowac wyglad ekranuSkladaniaZlecen i ekranKoncowy
 * -zmienianie tabel - enum!
 * -moze inna obsluga wyjatku
 * -warningi w ekranach
 * -moze przekazywac obiekty Oferta?
 */ 
