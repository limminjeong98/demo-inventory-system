const fs = require('fs')



function main() {
    const result = fs.readFileSync('./result.txt', 'utf-8')
    const numbers = result.split('\n').map(Number)

    // [5, 4, 3, 2, 5]
    // [0, 1, 2, 3, 0]
    const duplicates = numbers.filter((number, index) => {
        const foundIndex = numbers.indexOf(number)
        return foundIndex !== index
    })
    if (duplicates.length !== 0) {
        throw new Error('Find duplicates')
    }
}

main()
// 실행 : $ node index.js