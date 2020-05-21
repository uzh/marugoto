## page.json
*short description*

### id
*short description*

**n/a**

### title
*short description*

**string**

### continueRandomly
*short description*

**boolean**

### timerVisible
*short description*

**boolean**

### endOfTopic
*short description*

**boolean**

### autoTransitionOnTimerExpiration
*short description*

**boolean**

### time
*short description*

**object**

properties:

* "time": *int*

### money
*short description*

**object**

properties:

* "amount": *int*

### chapter
*short description*

**n/a**

## pageTransition.json
*short description*

### id
*short description*

**n/a**

### from
*short description*

**string; file path**

### to
*short description*

**string; file path**

### buttonText
*short description*

**string**

### time
*short description*

**object**

properties:

* "time": *int*

### money
*short description*

**object**

properties:

* "amount": *int*

### criteria
*short description*

**array**

*short description*

* {
  "pageCriteria": "visited",

  "exerciseCriteria": null,

  "mailCriteria": null,

  "affectedExercise": null,

  "affectedPage": *string; file path*,

  "affectedMail": null
}

*short description*

* {
  "pageCriteria": "notVisited",

  "exerciseCriteria": null,

  "mailCriteria": null,

  "affectedExercise": null,

  "affectedPage": *string; file path*,

  "affectedMail": null
}

*short description*

* {
  "pageCriteria": "notVisitedAny",

  "exerciseCriteria": null,

  "mailCriteria": null,

  "affectedExercise": null,

  "affectedPagesIds": [*string; file path*, *string; file path*],

  "affectedMail": null
}

*short description*

* {
  "pageCriteria": "visitedAny",

  "exerciseCriteria": null,

  "mailCriteria": null,

  "affectedExercise": null,

  "affectedPagesIds": [*string; file path*, *string; file path*],

  "affectedMail": null
}

*short description*

* {
  "pageCriteria": null,

  "exerciseCriteria": null,

  "mailCriteria": null,

  "affectedExercise": null,

  "affectedPage": null,

  "affectedDialogResponse":[*string; file path*, *string; file path*],

  "affectedMail": null
}

*short description*

* {
  "pageCriteria": null,

  "exerciseCriteria": "correctInput",

  "mailCriteria": null,

  "affectedExercise": *string; file path*,

  "affectedPage": null,

  "affectedMail": null
}


## textComponent.json
*short description*

### id
*short description*

**n/a**

### numberOfColumns
*short description*

**integer**

### offsetColumns
*short description*

**integer**

### renderOrder
*short description*

**integer**

### page
*short description*

**string; file path**

### markdownContent
*short description*

**string**

##videoComponent.json
*short description*

### id
*short description*

**n/a**

### numberOfColumns
*short description*

**integer**

### offsetColumns
*short description*

**integer**

### renderOrder
*short description*

**integer**

### page
*short description*

**string; file path**

### video
*short description*

**string; file path**

### caption
*short description*

**string**

## imageComponent.json
*short description*

### id
*short description*

**n/a**

### numberOfColumns
*short description*

**integer**

### offsetColumns
*short description*

**integer**

### renderOrder
*short description*

**integer**

### page
*short description*

**string; file path**

### images
*short description*

**array**

properties:

* *string; file path*


### imageViewRectangle
*short description*

**n/a**

### zoomable
*short description*

**boolean**

### caption
*short description*

**string**

## notebookEntry.json
*short description*

### id
*short description*

**n/a**

### title
*short description*

**string**

### page
*short description*

**string; file path**

### dialogResponse.json
*short description*

### mail
*short description*

**string; file path**

## textExercise.json
*short description*

### id
*short description*

**n/a**

### numberOfColumns
*short description*

**integer**

### offsetColumns
*short description*

**integer**

### renderOrder
*short description*

**integer**

### showInNotebook
*short description*

**boolean**

### showInNotebookAt
*short description*

**string**

options:

* "pageEnter"
* "pageExit"
* null

### descriptionForNotebook
*short description*

**string**

### page
*short description*

**string; file path**

### exerciseState
*short description*

**n/a**

### placeholderText
*short description*

**string**

### defaultText
*short description*

**string**

### maxLength
*short description*

**integer**

### textSolutions
*short description*

**array**

*short description*

* {
  "textToCompare": null,

  "minLength": *integer*,

  "mode": "length"
}

*short description*

* {
  "textToCompare": *string*,

  "minLength": null,

  "mode": "fuzzyComparison"
}


## character.json
*short description*

### id
*short description*

**n/a**

### salutation
*short description*

**string**

### firstName
*short description*

**string**

### lastName
*short description*

**string**

### mail
*short description*

**n/a**

### image
*short description*

**string; file path**

## mail.json
*short description*

### id
*short description*

**n/a**

### from
*short description*

**string; file path**

### openOnReceive
*short description*

**boolean**

### receiveAfter
*short description*

**object**

properties:

* "time": *int*

### showInNotebook
*short description*

**boolean**

### page
*short description*

**string; file path**

### subject
*short description*

**string**

### body
*short description*

**string**

## dialog.json
*short description*

### id
*short description*

**n/a**

### from
*short description*

**string; file path**

### openOnReceive
*short description*

**boolean**

### receiveAfter
*short description*

**object**

properties:

* time: *int*

### page
*short description*

**string; file path**

### speech
*short description*

**string; file path**

## dialogResponse
*short description*

### id
*short description*

**n/a**

### buttonText
*short description*

**string**

### from
*short description*

**string; file path**

### to
*short description*

**string; file path**

### pageTransition
*short description*

**string; file path**

## dialogSpeech.json
*short description*

### id
*short description*

**n/a**

### markdownContent
*short description*

**string**

### answers
*short description*

**n/a**

## chapter.json
*short description*

### id
*short description*

**n/a**

### title
*short description*

**string**

### icon
*short description*

**string; file path**

## dateExercise.json
*short description*

### id
*short description*

**n/a**

### numberOfColumns
*short description*

**integer**

### offsetColumns
*short description*

**integer**

### renderOrder
*short description*

**integer**

### showInNotebook
*short description*

**boolean**

### showInNotebookAt
*short description*

**string**

options:

* "pageEnter"
* "pageExit"
* null

### page
*short description*

**string; file path**

### label
*short description*

**string**

### solution
*short description*

**object**

properties:

* correctDate: *datetime string DD.MM.YYYY*

## checkboxExercise.json
*short description*

### id
*short description*

**n/a**

### numberOfColumns
*short description*

**integer**

### offsetColumns
*short description*

**integer**

### renderOrder
*short description*

**integer**

### page
*short description*

**string; file path**

### exerciseState
*short description*

**n/a**

### options
*short description*

**array**

properties:

* {"text": *string*}
* {"text": *string*, "correct": *boolean*}

### minimumSelected
*short description*

**integer**

### solutionMode
*short description*

**string**

properties:

* "minimum"

## audioComponent.json
*short description*

### id
*short description*

**n/a**

### numberOfColumns
*short description*

**integer**

### offsetColumns
*short description*

**integer**

### renderOrder
*short description*

**integer**

### page
*short description*

**string; file path**

### audio
*short description*

**string; file path**

## radioButtonExercise.json
*short description*

### id
*short description*

**n/a**

### numberOfColumns
*short description*

**integer**

### offsetColumns
*short description*

**integer**

### renderOrder
*short description*

**integer**

### page
*short description*

**string; file path**

### exerciseState
*short description*

**n/a**

### options
*short description*

**array**

properties:

* {"text": *string*}
* {"text": *string*, "correct": *boolean* }

## topic.json
*short description*

### id
*short description*

**n/a**

### title
*short description*

**string**

### image
*short description*

**string; file path**

### active
*short description*

**boolean**

### startPage
*short description*

**string; file path**
