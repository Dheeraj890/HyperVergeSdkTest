 // Sort by date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        planUnitsBySubFunds.sort(Comparator.comparing(d -> {
            try {
                return sdf.parse(d.getString("date"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        // Map to track latest known balanceUnits
        Map<String, Double> latestBalances = new LinkedHashMap<>();

        // Final result
        List<Document> transformed = new ArrayList<>();

        for (Document entry : planUnitsBySubFunds) {
            String date = entry.getString("date");
            List<Document> currentUnits = (List<Document>) entry.get("unitsBySubFundId");

            // Update latest balances
            for (Document unit : currentUnits) {
                String subFundId = unit.getString("subFundId");
                Double balance = unit.getDouble("balanceUnits");
                latestBalances.put(subFundId, balance);
            }

            // Create new list with all known subFundIds up to this date
            List<Document> newUnitsList = new ArrayList<>();
            for (Map.Entry<String, Double> e : latestBalances.entrySet()) {
                String subFundId = e.getKey();
                // If the current date has an updated value, use that
                Double balance = latestBalances.get(subFundId);
                for (Document unit : currentUnits) {
                    if (unit.getString("subFundId").equals(subFundId)) {
                        balance = unit.getDouble("balanceUnits");
                        break;
                    }
                }
                newUnitsList.add(new Document("subFundId", subFundId).append("balanceUnits", balance));
            }

            transformed.add(new Document("date", date).append("unitsBySubFundId", newUnitsList));
        }

        // Print final result
        for (Document d : transformed) {
            System.out.println(d.toJson());
        }
