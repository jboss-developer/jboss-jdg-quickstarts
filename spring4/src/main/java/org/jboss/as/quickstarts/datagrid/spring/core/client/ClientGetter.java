package org.jboss.as.quickstarts.datagrid.spring.core.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Extremely slow Clients repository.
 *
 * @author Sebastian Laskawiec
 */
public class ClientGetter {

    private static final String[] FIRST_NAMES = {"Jan", "Charlotte", "Eulalia", "Devora", "Tamera", "Leandra", "Malissa", "Concepcion", "Hee", "Brenton", "Particia", "Hassan", "Alma", "Jody", "Kum", "Suzan", "Jonas", "Nakita", "Antonia", "Giuseppina"};

    private static final String[] LAST_NAMES = {"Kowalski", "Dicesare", "Corsetti", "Padgett", "Norberg", "Aldrich", "Berman", "Hirshfeld", "Perko", "Leopold", "Christian-herot", "Huttenback", "Castillo", "Benabou", "Kee", "Kenward", "Ramey"};

    private static final String[] COFFEES = {"Latte", "Cappuccino", "Espresso", "Americano"};

    private Random randomSeed = new Random();

    public List<Client> getBestClients() {
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            String firstName = FIRST_NAMES[randomSeed.nextInt(FIRST_NAMES.length)];
            String lastName = LAST_NAMES[randomSeed.nextInt(LAST_NAMES.length)];
            String coffee = COFFEES[randomSeed.nextInt(COFFEES.length)];
            int numberOfOrders = randomSeed.nextInt(100);

            clients.add(new Client(i, firstName, lastName, coffee, numberOfOrders));
            randomDelay();
        }
        return clients;
    }

    protected void randomDelay() {
        try {
            TimeUnit.MILLISECONDS.sleep(randomSeed.nextInt(200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
