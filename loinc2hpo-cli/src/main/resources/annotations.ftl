<!doctype html>
<html class="no-js" lang="">

<head>
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>LOINC2HPO Annotations</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <style>
      table {
        border-collapse: collapse;
        width: 90%;
      }
      th, td {
        text-align: left;
        padding: 0.4rem 0.5rem 0.25rem;
      }
      th {
         background-color: #e0e3ea;
         border-bottom: 1px solid white;
      }

      table.l2h {
          width:  90%;
          min-width: 50%;
          margin-left: auto;
          margin-right: auto;
          border: 1px solid black;
      }
      table.l2h td {
         line-height: 40px;
      }
      table.l2h th {
         font-size: 1.5rem;
      }

      table.l2h tr:nth-child(even) {
         background: #F5F5F5
      }

      table.l2h tr:nth-child(odd) {
         background: #FFF
      }
    </style>
  <title>Welcome!</title>
</head>
<body>
  <#-- Greet the user with his/her name -->
  <h1>LOINC2HPO Annotations</h1>
  <p>The file was produced from the
    <a href="https://github.com/TheJacksonLaboratory/loinc2hpoAnnotation" target="__blank">loinc2hpoAnnotation</a>
    project biocuration data.</p>
  <table class="l2h">
    <tr>
        <th>Loinc Item</th>
        <th>Scale/Outcome</th>
        <th>HPO term</th>
        <th>biocuration</th>
    </tr>
    <#list loincItems as itm>

        <tr>
          <td >${itm.loincAnchor}</td>
            <td>${itm.loincscale}/${itm.outcome}</td>
            <td>${itm.hpoAnchor}</td>
             <td>${itm.biocuration}</td>
             </tr>

       </#list>
    </table>
</body>[BR]
</html>