function inspect(value) {
  return String(value)
}

inspect.custom = typeof Symbol === 'function' ? Symbol.for('nodejs.util.inspect.custom') : undefined

module.exports = inspect
