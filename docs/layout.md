# Page layout

A page in *Marugoto*, the engine *Lives in Transit* is based on, is constructed based on 12 evenly spaced vertical columns. The size and position of every text, image, exercise etc. is set using these. *Marugoto* fills a page from top-left to bottom-right, similar to how you read for example english. 

The module-files of a *Lives in Transit*-page (**textComponent**, **imageComponent**, **textExercise**, etc.) are all placed using three variables:

* numberOfColumns (sets the width of a module)
* offsetColumns (sets the space left of a module)
* renderOrder (the order in which *Marugoto* loads each mdoule)

If you for example want to place a big **textComponent** in the middle of the page as the very first thing, you would use the following JSON code:

```json
{
  "id": null,
  "numberOfColumns": 10,
  "offsetColumns": 1,
  "renderOrder": 1,
  "showInNotebook": false,
  "showInNotebookAt": null,
  "page": null,
  "markdownContent": "Lorem ipsum..."
}
```

To have the module placed as the first thing on the page you assign the *renderOrder* the value "1". If you want to have something centered on the page you'll have to do some basic math. In this case, to have the text with the width of "10" centered, you have to substract the width from 12 and divide this result by two to get the *offsetColumns*. In this case: `12 - 10 = 2` `2/2 = 1`. 

It is only possible to assign a positive integer below 12 as values, so for example "3", "6" or "10", but not "1.5", "-3", etc.

If you want to place two modules next to each other, you'll have to make sure they fit in the 12 columns. For example, if you want to place a **textComponent** on the right side of a **imageComponent** and both be the same width, you assign both modules the *numberOfColumns* "5" with an *offsetColumns* value of "1". To place the **textComponent** on the right side of the **imageComponent**, the *renderOrder* has to be one integer higher. In this example the **imageComponent** would have the *renderOrder* "1" and the **textComponent** would have the *renderOrder* "2".

This also applies to **textExercise** and multi-media-modules.
