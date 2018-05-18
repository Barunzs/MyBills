package bill.com.mybills.model

internal class Item {


    constructor(particulars: String?, weight: Double, goldRate: Double, makingCharge: Double) {
        this.particulars = particulars
        this.weight = weight
        this.goldRate = goldRate
        this.makingCharge = makingCharge
    }

    public var particulars: String? = null
    public var weight = 0.0
    public var goldRate = 0.0
    private var amtGold = 0.0
    public var makingCharge = 0.0

    public fun setAmtGold(amtGold:Double){
        this.amtGold = amtGold
    }

}