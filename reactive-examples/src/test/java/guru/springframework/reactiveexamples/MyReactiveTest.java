package guru.springframework.reactiveexamples;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * Created by tarashrynchuk on 6/5/18.
 */
public class MyReactiveTest {
	private Person laura = new Person("Laura", "Palmer");
	private Person maura = new Person("Maura", "Palmer");
	private Person zaura = new Person("Zaura", "Palmer");

	@Test public void monoTest() {
		Mono<Person> just = Mono.just(laura);
		Person person = just.block();
		System.out.println(person);
	}

	@Test public void mapTest() {
		Mono<Person> personMono = Mono.just(maura);
		PersonCommand personCommand = personMono.map(person -> {
			return new PersonCommand(person);
		}).block();
		System.out.println(personCommand.sayMyName());
	}

	@Test public void fluxTest() {
		Flux<Person> flux = Flux.just(laura, maura, zaura);
		flux.subscribe(person -> {
			System.out.println(person.sayMyName());
		});
	}

	@Test public void filterTest() {
		Flux<Person> flux = Flux.just(laura, maura, zaura);
		flux.filter(person -> person.getFirstName().equals("Laura")).
				subscribe(person -> {
					System.out.println(person.sayMyName());
				});
	}

	@Test public void delayTest() throws Exception {
		Flux<Person> flux = Flux.just(laura, zaura);
		flux.delayElements(Duration.ofSeconds(1)).subscribe(
				person -> {
					System.out.println(person.sayMyName());
				}
		);
		Thread.currentThread().sleep(4000);
	}

	@Test public void countDown() throws Exception {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		Flux<Person> flux = Flux.just(laura, zaura);
		flux.delayElements(Duration.ofSeconds(1))
				.filter(person -> person.getFirstName().startsWith("L"))
				.doOnComplete(countDownLatch::countDown).subscribe(
				person -> System.out.println(person.getFirstName())
		);

		countDownLatch.await();
	}
}
