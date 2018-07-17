library(dplyr)
library(readxl)
data <- read_excel("/Users/zhangx/Downloads/LOINC_2/Accessory/PanelsAndForms/LOINC_263_PanelsAndForms.xlsx", sheet = 1, na = "NA")
write.table(data, "/Users/zhangx/Downloads/LOINC_2/Accessory/PanelsAndForms/testExcel.csv",sep = "****", quote = FALSE, row.names = FALSE)