package ch.uzh.marugoto.controller;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.data.entity.Character;
import ch.uzh.marugoto.data.entity.Page;
import ch.uzh.marugoto.data.repository.CharacterRepository;
import ch.uzh.marugoto.data.repository.PageRepository;

@RestController
public class DataTestController {

	@Autowired
	private CharacterRepository characterRepository;

	@Autowired
	private PageRepository pagesRepository;

	@Autowired
	private ArangoOperations operations;

	@RequestMapping("/createCharacters")
	public Collection<Character> createCharacters() {
		var characters = Arrays.asList(new Character("Ned", "Stark", false, 41),
				new Character("Robert", "Baratheon", false), new Character("Jaime", "Lannister", true, 36),
				new Character("Catelyn", "Stark", false, 40), new Character("Cersei", "Lannister", true, 36),
				new Character("Daenerys", "Targaryen", true, 16), new Character("Jorah", "Mormont", false),
				new Character("Petyr", "Baelish", false), new Character("Viserys", "Targaryen", false),
				new Character("Jon", "Snow", true, 16), new Character("Sansa", "Stark", true, 13),
				new Character("Arya", "Stark", true, 11), new Character("Robb", "Stark", false),
				new Character("Theon", "Greyjoy", true, 16), new Character("Bran", "Stark", true, 10),
				new Character("Joffrey", "Baratheon", false, 19), new Character("Sandor", "Clegane", true),
				new Character("Tyrion", "Lannister", true, 32), new Character("Khal", "Drogo", false),
				new Character("Tywin", "Lannister", false), new Character("Davos", "Seaworth", true, 49),
				new Character("Samwell", "Tarly", true, 17), new Character("Stannis", "Baratheon", false),
				new Character("Melisandre", null, true), new Character("Margaery", "Tyrell", false),
				new Character("Jeor", "Mormont", false), new Character("Bronn", null, true),
				new Character("Varys", null, true), new Character("Shae", null, false),
				new Character("Talisa", "Maegyr", false), new Character("Gendry", null, false),
				new Character("Ygritte", null, false), new Character("Tormund", "Giantsbane", true),
				new Character("Gilly", null, true), new Character("Brienne", "Tarth", true, 32),
				new Character("Ramsay", "Bolton", true), new Character("Ellaria", "Sand", true),
				new Character("Daario", "Naharis", true), new Character("Missandei", null, true),
				new Character("Tommen", "Baratheon", true), new Character("Jaqen", "H'ghar", true),
				new Character("Roose", "Bolton", true), new Character("The High Sparrow", null, true));

		characterRepository.saveAll(characters);

		return characters;
	}

	@RequestMapping("/countCharacters")
	public long countCharacters() {
		long count = characterRepository.count();
		return count;
	}

	@RequestMapping("/loadCharacters")
	public Iterable<Character> loadCharacters() throws Exception {
		// first drop the database so that we can run this multiple times with the same
		// dataset
		operations.dropDatabase();

		this.createCharacters();

		final Iterable<Character> characters = characterRepository.findAll();

		return characters;
	}

	@RequestMapping("/createPages")
	public Collection<Page> createPages() {
		// first drop the database so that we can run this multiple times with the same dataset
		operations.dropDatabase();

		var pages = Arrays.asList(new Page("Tutorial 1", false), new Page("End of story", true));
		pagesRepository.saveAll(pages);
		return pages;
	}

	@RequestMapping("/countPages")
	public long countPages() {
		long count = pagesRepository.count();
		return count;
	}

	@RequestMapping("/loadPages")
	public Iterable<Page> loadPages() throws Exception {
		final Iterable<Page> pages = pagesRepository.findAll();
		return pages;
	}
}
