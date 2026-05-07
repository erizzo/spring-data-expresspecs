package expresspecs;

import rizzoweb.spring.jpa.DataIntegrationTest;

interface BaseIntegrationTest<E> extends CustomerDataFactory, DataIntegrationTest<E> {

}
