const BASE_URL = '/api'

/**
 * @typedef {Object} CalculateRequest
 * @property {string} query
 */

/**
 * @typedef {Object} CalculateSuccessResponse
 * @property {'success'} type
 * @property {string} result
 */

/** @typedef {Object} CalculateErrorResponse
 * @property {'error'} type
 * @property {string} message
 */

/** @typedef {(CalculateSuccessResponse | CalculateErrorResponse)} CalculateResponse */

/**
 * @typedef {Object} HistoryResponseItem
 * @property {number} id
 * @property {string} query
 */

/** @typedef {HistoryResponseItem[]} HistoryResponse */

/** @typedef {string[]} CalculatorHistory */

class CalculateError extends Error {}

/** @type {HTMLInputElement} */
const inputElement = document.getElementById('input')

/** @type {HTMLCollectionOf<HTMLButtonElement>} */
const inputButtons = document.getElementsByClassName('btn-input')

/** @type {HTMLButtonElement} */
const clearButton = document.getElementById('btn-clr')

/** @type {HTMLButtonElement} */
const eqButton = document.getElementById('btn-eq')

/** @type {HTMLButtonElement} */
const bsButton = document.getElementById('btn-bs')

/** @type {HTMLElement} */
const historyItems = document.getElementById('history-items')

for (const inputButton of inputButtons) {
  inputButton.addEventListener('click', appendCharacterHandler)
}

clearButton.addEventListener('click', clearInput)

bsButton.addEventListener('click', bsHandler)

eqButton.addEventListener('click', calculateHandler)

const api = {
  /**
   * @returns {Promise<CalculatorHistory>} calculation history
   */
  async fetchAll() {
    const url = `${BASE_URL}/history`

    const response = await fetch(url)

    /** @type {HistoryResponse} */
    const json = await response.json()

    /** @type {History} */
    const result = []
    for (const item of json) {
      result.push(item.query)
    }

    return result
  },

  /**
   * @param {string} query - query to calculate
   * @throws {CalculateError}
   * @returns {Promise<number>} result of the query
   */
  async calculate(query) {
    const response = await fetch(`${BASE_URL}/calculate`, {
      headers: {
        'content-type': 'application/json',
      },
      body: JSON.stringify({ query }),
      method: 'POST',
    })
    /** @type {CalculateResponse} */
    const json = await response.json()
    if (json.type === 'success') return parseFloat(json.result)
    if (json.type === 'error') throw new CalculateError(json.message)
    throw new CalculateError('Unknown error')
  },
}

const render = {
  /**
   * @param {string} text
   * @returns {HTMLElement}
   */
  generateCard(text) {
    const article = document.createElement('article')

    const span = document.createElement('span')
    span.innerText = text

    article.appendChild(span)

    article.addEventListener('click', historyClickHandler)

    return article
  },

  /**
   * @param {string} text
   * @param {boolean | undefined} above
   */
  renderItem(text, above = false) {
    const card = this.generateCard(text)

    /** @type {InsertPosition} */
    const position = above ? 'afterbegin' : 'beforeend'
    historyItems.insertAdjacentElement(position, card)
  },

  /**
   * @param {string[]} items
   */
  renderHistory(items) {
    for (const item of items) {
      this.renderItem(item)
    }
  },
}

const state = {
  clearWhenAppend: false,
}

/**
 * @param {MouseEvent} e
 */
function historyClickHandler(e) {
  /** @type {HTMLElement} */
  const element = e.target

  inputElement.value = element.innerText
}

/**
 * @param {MouseEvent} e
 */
function appendCharacterHandler(e) {
  /** @type {HTMLInputElement} */
  const element = e.target

  if (state.clearWhenAppend) clearInput()

  inputElement.value += element.textContent
}

function clearInput() {
  inputElement.value = ''
}

function bsHandler() {
  inputElement.value = inputElement.value.slice(0, -1)
}

async function calculateHandler() {
  const query = inputElement.value
  try {
    const result = await api.calculate(query)
    inputElement.value = result

    render.renderItem(query, true)
  } catch (e) {
    if (e instanceof CalculateError) {
      inputElement.value = `Ошибка: ${e.message}`
    } else {
      inputElement.value = 'Ошибка: неизвестная ошибка'
      console.log(e)
    }
    state.clearWhenAppend = true
  }
}

api
  .fetchAll()
  .then((history) => render.renderHistory(history))
  .catch((error) => {
    alert('Произошла ошибка при загрузке истории')
    console.error(error)
  })
