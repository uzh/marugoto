package ch.uzh.marugoto.core.data.entity.topic;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

import com.arangodb.springframework.annotation.Edge;
import com.arangodb.springframework.annotation.From;
import com.arangodb.springframework.annotation.To;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * It connects the pages in the order they can be used.
 * 
 */
@Edge
@JsonIgnoreProperties({"from", "to", "criteria", "time", "money"})
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
	private int renderOrder = 1;

	public PageTransition() {
		super();
	}
	
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

	public void setFrom(Page from) {
		this.from = from;
	}

	public void setTo(Page to) {
		this.to = to;
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

	public VirtualTime getTime() {
		return time;
	}

	public void setTime(VirtualTime time) {
		this.time = time;
	}

	public Money getMoney() {
		return money;
	}

	public void setMoney(Money money) {
		this.money = money;
	}

	public boolean hasCriteria() {
		return !criteria.isEmpty();
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
		boolean equals = false;

		if (o instanceof PageTransition) {
			var pageTransition = (PageTransition) o;
			equals = id.equals(pageTransition.id);
		}

		return equals;
	}

	public int getRenderOrder() {
		return renderOrder;
	}

	public void setRenderOrder(int renderOrder) {
		this.renderOrder = renderOrder;
	}
}