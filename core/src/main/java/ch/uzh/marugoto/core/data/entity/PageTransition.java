package ch.uzh.marugoto.core.data.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;

import com.arangodb.springframework.annotation.Edge;
import com.arangodb.springframework.annotation.From;
import com.arangodb.springframework.annotation.To;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * It connects the pages in the order they can be used.
 * 
 */
@Edge
@JsonIgnoreProperties({"from", "to"})
public class PageTransition {

	@Id
	private String id;
	@From
	private Page from;
	@To
	private Page to;
	private String buttonText;
	private VirtualTime time;
	private Money money;
	private List<Criteria> criteria;

	@PersistenceConstructor
	public PageTransition(Page from, Page to, String buttonText) {
		super();
		this.from = from;
		this.to = to;
		this.buttonText = buttonText;
		this.criteria = new ArrayList<>();
	}

	public PageTransition(Page from, Page to, String buttonText, VirtualTime time, Money money) {
		this(from, to, buttonText);
		this.time = time;
		this.money = money;
	}

	public String getId() {
		return id;
	}

	public Page getFrom() {
		return from;
	}

	public Page getTo() {
		return to;
	}

	public String getButtonText() {
		return buttonText;
	}

	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public VirtualTime getVirtualTime() {
		return time;
	}

	public void setVirtualTime(VirtualTime time) {
		this.time = time;
	}

	public Money getMoney() {
		return money;
	}

	public void setMoney(Money money) {
		this.money = money;
	}

	public List<Criteria> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<Criteria> criteria) {
		this.criteria = criteria;
	}

	public void addCriteria(Criteria criteria) {
		this.criteria.add(criteria);
	}

	public boolean equals(Object o) {
		var pageTransition = (PageTransition) o;
		return id.equals(pageTransition.id);
	}
}