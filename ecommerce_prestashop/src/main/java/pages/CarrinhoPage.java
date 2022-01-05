package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CarrinhoPage {

	private WebDriver driver;

	private By nomeProduto = By.cssSelector("div.product-line-info a");

	private By precoProduto = By.cssSelector("span.price");

	private By tamanhoProduto = By
			.xpath("//div[contains(@class,'product-line-grid-body')]//div[3]/span[contains(@class,'value')]");

	private By corProduto = By
			.xpath("//div[contains(@class,'product-line-grid-body')]//div[4]/span[contains(@class,'value')]");

	private By inputQuantidadeProduto = By.cssSelector("input.js-cart-line-product-quantity");

	private By subTotalProduto = By.cssSelector("span.product-price strong");

	private By numeroItensTotal = By.cssSelector("span.js-subtotal");

	private By subTotalTotal = By.cssSelector("#cart-subtotal-products span.value");

	private By shippingTotal = By.cssSelector("#cart-subtotal-shipping span.value");

	private By totalTaxExclTotal = By
			.cssSelector("div.cart-summary-totals div.cart-summary-line:nth-child(1) span.value");

	private By totalTaxIncTotal = By
			.cssSelector("div.cart-summary-totals div.cart-summary-line:nth-child(2) span.value");

	private By taxesTotal = By.cssSelector("div.cart-summary-totals div.cart-summary-line:nth-child(3) span.value");
	
	private By botaoProceedToCheckout = By.cssSelector("a.btn-primary");

	// Constructor initializing the driver
	public CarrinhoPage(WebDriver driver) {
		this.driver = driver;
	}

	public String obter_nomeProduto() {
		return driver.findElement(nomeProduto).getText();
	}

	public String obter_precoProduto() {
		return driver.findElement(precoProduto).getText();
	}

	public String obter_tamanhoProduto() {
		return driver.findElement(tamanhoProduto).getText();
	}

	public String obter_corProduto() {
		return driver.findElement(corProduto).getText();
	}

	public String obter_inputQuantidadeProduto() {
		return driver.findElement(inputQuantidadeProduto).getAttribute("value");
	}

	public String obter_subTotalProduto() {
		return driver.findElement(subTotalProduto).getText();
	}

	public String obter_numeroItensTotal() {
		return driver.findElement(numeroItensTotal).getText();
	}

	public String obter_subTotalTotal() {
		return driver.findElement(subTotalTotal).getText();
	}

	public String obter_shippingTotal() {
		return driver.findElement(shippingTotal).getText();
	}

	public String obter_totalTaxExclTotal() {
		return driver.findElement(totalTaxExclTotal).getText();
	}

	public String obter_totalTaxIncTotal() {
		return driver.findElement(totalTaxIncTotal).getText();
	}

	public String obter_taxesTotal() {
		return driver.findElement(taxesTotal).getText();
	}
	
	public CheckoutPage clicarBotaoProceedToCheckout() {
		driver.findElement(botaoProceedToCheckout).click();
		return new CheckoutPage(driver);
	}

}
