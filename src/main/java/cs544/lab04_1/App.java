package cs544.lab04_1;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;

import cs544.lab04_1.model.Airline;
import cs544.lab04_1.model.Airplane;
import cs544.lab04_1.model.Airport;
import cs544.lab04_1.model.Flight;

public class App {
	private static Logger logger = Logger.getLogger(App.class);;

	private static final EntityManagerFactory emf;

	static {
		try {
			emf = Persistence.createEntityManagerFactory("cs544");
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static void main(String[] args) {
		EntityManager em = null;
		EntityTransaction tx = null;

		// fill the database
		fillDataBase();

		// Flights leaving USA capacity > 500
		try {
			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();


			List<Flight> flights500 = em
					.createQuery("from Flight as f where f.origin.country = 'USA' AND f.airplane.capacity>500")
					.getResultList();

			logger.trace("Flights leaving USA capacity > 500:");
			printFlight(flights500);
			em.flush();
			List<Flight> flight747 = em
					.createQuery("from Flight f where f.airplane.model = '747' " +
							"AND f.airline <> 'Star Alliance'").getResultList();

			logger.trace("All fights using 747 planes that don’t belong to ‘Star Alliance’:");
			printFlight(flight747);
			tx.commit();
		} catch (PersistenceException e) {
			if (tx != null) {
				logger.error("Rolling back:", e);
				tx.rollback();
			}
		} finally {
			if (em != null) {
				em.close();
			}
		}

		System.exit(0);
	}

	public static void printFlight(List<Flight> flights){
		logger.trace(String.format("%-9s%-31s%-31s", "Flight:", "Departs:", "Arrives:"));
		for (Flight flight : flights) {
			logger.trace(String.format("%-7s  %-12s %7s %8s  %-12s %7s %8s", flight.getFlightnr(),
					flight.getOrigin().getCity(), flight.getDepartureDate(), flight.getDepartureTime(),
					flight.getDestination().getCity(), flight.getArrivalDate(), flight.getArrivalTime()));
		}
	}
	public static void fillDataBase() {
		EntityManager em = null;
		EntityTransaction tx = null;

		try {

			Airport mco = new Airport("MCO", "Orlando", "Florida", "USA");
			Airport mia = new Airport("MIA", "Miami", "Florida", "USA");

			Airplane airplane1 = new Airplane("56789", "A380", 519);

			Airline airline1 = new Airline("Delta");

			Flight flight1 = new Flight("DE 36", "08/06/2009", "7:10 pm", "06/25/2015", "9:00 am", airline1, mia, mco,
					airplane1);

			em = emf.createEntityManager();
			tx = em.getTransaction();
			tx.begin();

			em.persist(flight1);

			tx.commit();

		} catch (PersistenceException e) {
			if (tx != null) {
				logger.error("Rolling back", e);
				tx.rollback();
			}
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}

}
