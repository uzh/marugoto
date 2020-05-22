# Using textExercise

One key feature of *Marugoto* is the **textExercise**-module. It allows you, the game-creater, to task the player with documenting their thoughts about for example a video. 

To ensure that a page with a **textExercise** is working properly, the following few things have to be made sure.

### Files

There have to be a **textExercise** (obviously) and a **notebookEntry** file in the folder. 

### Contents of Files

The following code is an example for a **textExercise**:

```json
{
  "id": null,
  "numberOfColumns": 8,
  "offsetColumns": 2,
  "renderOrder": 3,
  "showInNotebook": true,
  "showInNotebookAt": "pageExit",
  "descriptionForNotebook": null,
  "page": null,
  "exerciseState": null,
  "placeholderText": "Take some notes",
  "defaultText": null,
  "maxLength": null,
  "textSolutions": []
}
```

This module has a width of **8**, is centered on the page (`8 + 2 x 2 = 12`), and will be rendered as the third module. The value for *showInNotebook* determines, if the content of the **textExercise** will later be visible in the player's notebook. There is no real reason, why the player's thoughts he's written down shouldn't be available at a later stage, so it's best to keep it `true`. *showInNotebookAt* determines when the contents will be visible as an entry in the notebook on the right side. (HJ: Martin, can you expand on that? Maybe if there are other possible values.)

The value of the variable *placeholderText* later shows as, well, placeholder for the player to click on and start typing away. It's best to give the player some sort of prompt if there isn't one already before. 

The following code is an example for a **notebookEntry**:

```json
{
  "id": null,
  "title": "The pharmacist of justice, a scale",
  "page": "chapter1/1.4.1/page.json",
  "dialogResponse": null,
  "mail": null
}
```

This file is necessary for the engine, *Marugoto*, to add the **textExercise** to the notebook. The two main variables for game-creators are *title*, which defines the title for the notebook entry, in this case "The pharmacist of justice, a scale", and *page*. For the notebook entry to work properly you have to define additionally, to which page it belongs. Because of this you have to have to specify the exact location of the **page** file, on which both the **textExercise** and the **notebookEntry** are, inside the repository. In this case it's `chapter1/1.4.1/page.json`.