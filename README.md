# drugs: Illicit drug data package for R

[![Build Status](https://travis-ci.org/njanetos/drugs.svg?branch=master)](https://travis-ci.org/njanetos/drugs) 

An R package that fetches and processes data on the prices of illicit drugs, scraped from deep web marketplaces. Also provides access to the STRIDE dataset in R. Despite its policy relevance, data on illicit drug prices is difficult to find. 

### Installation
------------

```
install.packages("devtools")
library("devtools")
install_github("njanetos/drugs")
library("drugs")
```

### Documentation
------------

* [Reference manual](http://njanetos.github.io/drugs/drugs.pdf)
* [Vignette: Constructing price indices](http://htmlpreview.github.io/?https://github.com/njanetos/drugs/master/vignettes/illicit_drug_price_indices.html)

### Example
------------

```
construct.index(market = c("evolution"),
           category = get.code('heroin'),
           units = "g")
```
Returns a price index for heroin computed by binning the data then finding the median price.
```
plot.index(market = c("evolution"),
           category = get.code('heroin'),
           units = "g")
title("Price of heroin")
```
Constructs and plots the price of heroin on the 'Evolution' drug marketplace, measured in grams.
```
connect.database("drugs_agora")
get.query("SELECT * FROM Listing L 
					INNER JOIN Listing_prices P 
					ON L.id = P.Listing_id LIMIT 10")
```
Connects to the database containing data on the 'Agora' marketplace, and downloads the first 10 price listings. 