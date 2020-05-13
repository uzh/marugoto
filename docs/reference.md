# Reference

## page.json
*short description*

### id
*short description*

**null**

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

**null**

### money
*short description*

**object**

properties:

* {"amount": *int*}

### chapter
*short description*

**null**

## pageTransition.json
*short description*

### id
*short description*

**null**

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

* "time": *int*

### money
*short description*

**object**

* "amount": *int*

### criteria
*short description*

**array**

* {
  "pageCriteria": *null* | *other* | *other*,

  "exerciseCriteria": *null* | *other* | *other*,
  
  "mailCriteria": *null* | *other* | *other*,

  "affectedExercise": *null* | *other* | *other*,

  "affectedPage": *null* | *other* | *other*,

  "affectedMail": *null* | *other* | *other*

}


## textComponent.json
*short description*

### id
*short description*

**null**

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

**null**

### markdownContent
*short description*

**string**

##videoComponent.json
*short description*

### id
*short description*

**null**

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

**null**

### video
*short description*

**string; file path**

### caption
*short description*

string

## imageComponent.json
*short description*

### id
*short description*

null

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

null
### images
*short description*

**array**

properties:

* string; file path


### imageViewRectangle
*short description*

null

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

null

### title
*short description*

**string**

### page
*short description*

**string; file path**

### dialogResponse
*short description*

null

### mail
*short description*

null

## textExercise.json
*short description*

### id
*short description*

null

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

* "pageExit"

### descriptionForNotebook
*short description*

null

### page
*short description*

null

### exerciseState
*short description*

null

### placeholderText
*short description*

**string**

### defaultText
*short description*

null

### maxLength
*short description*

null

### textSolutions
*short description*

**array**

## character.json
*short description*

### id
*short description*

null

### salutation
*short description*

null

### firstName
*short description*

**string**

### lastName
*short description*

**string**

### mail
*short description*

null

### image
*short description*

**string; file path**

## mail.json
*short description*

### id
*short description*

null

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

* ```time: *int*```

### showInNotebook
*short description*

**boolean**

### page
*short description*

null

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

null

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

* ```time: *int*```

### page
*short description*

null

### speech
*short description*

**string; file path**

## dialogResponse
*short description*

### id
*short description*

null

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

## dialogSpeech
*short description*

### id
*short description*

null

### markdownContent
*short description*

**string**

### answers
*short description*

null

## chapter.json
*short description*

### id
*short description*

null

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

null

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

null

### page
*short description*

null

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

null

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

null

### exerciseState
*short description*

null

### options
*short description*

**array**

properties:

* {"text": *string*}

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

null

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

null

### audio
*short description*

**string; file path**

## radioButtonExercise.json
*short description*

### id
*short description*

null

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

null

### exerciseState
*short description*

null

### options
*short description*

**array**

properties:

* {"text": *string*, "correct": *boolean* }

## topic.json
*short description*

### id
*short description*

null

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
