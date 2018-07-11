package ch.uzh.marugoto.backend.data.entity;

import org.springframework.data.annotation.Id;
import com.arangodb.springframework.annotation.Document;
import com.arangodb.springframework.annotation.HashIndex;

@Document("characters")
@HashIndex(fields = { "name", "surname" }, unique = true)
public class Character {

	@Id
	private String id;

	private String name;
	private String surname;
	private boolean alive;
	private Integer age;

	
	public String getId() {
		return id;
	}
	
	
	
	public Character() {
		super();
	}

	public Character(final String name, final String surname, final boolean alive) {
		super();
		this.name = name;
		this.surname = surname;
		this.alive = alive;
	}

	public Character(final String name, final String surname, final boolean alive, final Integer age) {
		super();
		this.name = name;
		this.surname = surname;
		this.alive = alive;
		this.age = age;
	}

	// getter & setter

	@Override
	public String toString() {
		return "Character [id=" + id + ", name=" + name + ", surname=" + surname + ", alive=" + alive + ", age=" + age
				+ "]";
	}

}