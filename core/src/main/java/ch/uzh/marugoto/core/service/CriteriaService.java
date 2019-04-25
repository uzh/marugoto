package ch.uzh.marugoto.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.marugoto.core.data.entity.application.User;
import ch.uzh.marugoto.core.data.entity.state.ExerciseState;
import ch.uzh.marugoto.core.data.entity.state.MailState;
import ch.uzh.marugoto.core.data.entity.state.PageState;
import ch.uzh.marugoto.core.data.entity.topic.Criteria;
import ch.uzh.marugoto.core.data.entity.topic.Mail;
import ch.uzh.marugoto.core.data.entity.topic.MailCriteriaType;
import ch.uzh.marugoto.core.data.entity.topic.PageTransition;
import ch.uzh.marugoto.core.data.entity.topic.TransitionChosenOptions;

@Service
public class CriteriaService {

	@Autowired
	private PageStateService pageStateService;
	@Autowired
	private ExerciseStateService exerciseStateService;
	@Autowired
	private MailService mailService;
	@Autowired
	private DialogService dialogService;

	/**
	 * Checks page transition if criteria is satisfied
	 *
	 * @param pageTransition
	 * @param user
	 * @return
	 */
	public boolean checkPageTransitionCriteria(PageTransition pageTransition, User user) {
		boolean criteriaSatisfied = true;
		if (pageTransition.hasCriteria()) {
			if (hasPageCriteria(pageTransition)) {
				// get user page states
				List<PageState> pageStateList = pageStateService.getPageStates(user);
				criteriaSatisfied = isPageCriteriaSatisfied(pageTransition, pageStateList);
			}

			if (hasExerciseCriteria(pageTransition)) {
				criteriaSatisfied = criteriaSatisfied
						&& isExerciseCriteriaSatisfied(pageTransition, user.getCurrentPageState());
			}

			if (hasMailCriteria(pageTransition)) {
				criteriaSatisfied = criteriaSatisfied && isMailCriteriaSatisfied(pageTransition, user);
			}

			if (hasDialogResponseCriteria(pageTransition)) {
				criteriaSatisfied = criteriaSatisfied && isDialogCriteriaSatisfied(pageTransition, user);
			}
		}

		return criteriaSatisfied;
	}

	/**
	 * Checks weather page transition has page criteria or not
	 * 
	 * @param pageTransition
	 * @return
	 */
	private boolean hasPageCriteria(PageTransition pageTransition) {
		return pageTransition.getCriteria().stream().anyMatch(Criteria::isForPage);
	}

	/**
	 * Checks weather page transition has exercise criteria or not
	 *
	 * @param pageTransition
	 * @return
	 */
	public boolean hasExerciseCriteria(PageTransition pageTransition) {
		return pageTransition.getCriteria().stream().anyMatch(Criteria::isForExercise);
	}

	/**
	 * Checks weather page transition has mail criteria or not
	 *
	 * @param pageTransition
	 * @return
	 */
	private boolean hasMailCriteria(PageTransition pageTransition) {
		return pageTransition.getCriteria().stream().anyMatch(Criteria::isForMail);
	}

	/**
	 * Checks weather page transition has specific mail criteria or not
	 *
	 * @param mail           Mail that criteria should check
	 * @param pageTransition checked PageTransition
	 * @return true / false
	 */
	public boolean hasMailReplyCriteria(Mail mail, PageTransition pageTransition) {
		return pageTransition.getCriteria().stream().anyMatch(criteria -> mail.equals(criteria.getAffectedMail())
				&& criteria.getMailCriteria() == MailCriteriaType.reply);
	}

	/**
	 * Checks weather page transition has dialog response criteria
	 *
	 * @param pageTransition to be checked
	 * @return boolean
	 */
	private boolean hasDialogResponseCriteria(PageTransition pageTransition) {
		return pageTransition.getCriteria().stream().anyMatch(Criteria::isForDialog);
	}

	/**
	 * Checks criteria that depends on the exercise correct / not correct / no input
	 *
	 * @param pageTransition
	 * @param pageState
	 * @return
	 */
	public boolean isExerciseCriteriaSatisfied(PageTransition pageTransition, PageState pageState) {
		boolean satisfied = false;

		for (Criteria criteria : pageTransition.getCriteria()) {
			if (criteria.isForExercise()) {
				ExerciseState exerciseState = exerciseStateService.getExerciseState(criteria.getAffectedExercise(),
						pageState);
				satisfied = exerciseStateService.exerciseSolved(exerciseState, criteria.getExerciseCriteria());
			}
		}

		return satisfied;
	}

	/**
	 * Checks criteria that depends on the page visited / not visited /
	 * timeExpiration
	 *
	 * @param pageTransition
	 * @param pageStateList
	 * @return
	 */
	private boolean isPageCriteriaSatisfied(PageTransition pageTransition, List<PageState> pageStateList) {
		boolean satisfied = false;

		for (Criteria criteria : pageTransition.getCriteria()) {
			if (criteria.isForPage()) {
				switch (criteria.getPageCriteria()) {
				case timeExpiration:
					PageState affectedPageState = pageStateList.stream()
							.filter(pageState -> pageState.getPage().equals(criteria.getAffectedPage())).findAny()
							.orElse(null);

					if (affectedPageState != null) {
						satisfied = affectedPageState.getPageTransitionStates().stream()
								.anyMatch(pageTransitionState -> pageTransitionState.getChosenBy()
										.equals(TransitionChosenOptions.autoTransition));
					}
					break;
				case visited:
					satisfied = pageStateList.stream()
							.anyMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
					break;
				case notVisited:
					satisfied = pageStateList.stream()
							.noneMatch(pageState -> pageState.getPage().equals(criteria.getAffectedPage()));
					break;
				case visitedAny:
					List<String> affectedPageIds = criteria.getAffectedPagesIds();
					for (String pageId : affectedPageIds) {
						if (pageStateList.stream().anyMatch(pageState -> pageState.getPage().getId().equals(pageId))) {
							satisfied = true;
							break;
						}
					}
				}
			}
		}

		return satisfied;
	}

	public boolean isMailCriteriaSatisfied(PageTransition pageTransition, User user) {
		boolean satisfied = false;
		for (Criteria criteria : pageTransition.getCriteria()) {
			if (criteria.isForMail()) {
				Optional<MailState> optionalMailState = mailService.getMailState(user, criteria.getAffectedMail());
				switch (criteria.getMailCriteria()) {
				case read:
					satisfied = optionalMailState.isPresent() && optionalMailState.get().isRead();
					break;
				case reply:
					satisfied = optionalMailState.isPresent() && optionalMailState.get().getMailReplyList().size() > 0;
				}
			}
		}

		return satisfied;
	}

	private boolean isDialogCriteriaSatisfied(PageTransition pageTransition, User user) {
		boolean satisfied = false;
		for (Criteria criteria : pageTransition.getCriteria()) {
			if (criteria.isForDialog()) {
				satisfied = dialogService.getDialogState(user, criteria.getAffectedDialogResponse()).isPresent();
			}
		}

		return satisfied;
	}
}
